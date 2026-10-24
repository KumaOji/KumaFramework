package com.kuma.cloud.console;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.ObjectMapper;

import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class BlogLifecycleTest {
    @TempDir Path root;

    BlogLifecycle create(int port) throws Exception {
        Path blog = root.resolve("front end/KumaBlog");
        Files.createDirectories(blog.resolve("node_modules/astro"));
        Files.writeString(blog.resolve("package.json"), "{}");
        Files.createDirectories(blog.resolve("scripts"));
        Files.writeString(blog.resolve("scripts/sync-knowledge.mjs"), "console.log('Knowledge synced');");
        Files.writeString(blog.resolve("node_modules/astro/astro.js"), """
                const http=require('node:http');
                const port=Number(process.argv[process.argv.indexOf('--port')+1]);
                http.createServer((req,res)=>res.end('Blog test')).listen(port,'127.0.0.1',()=>console.log('Blog ready'));
                """);
        return new BlogLifecycle(root.toString(), blog.toString(), port, new CommandRunner(), new ObjectMapper());
    }

    @Test void startsStopsAndLogsNodeProcessWithSpacesInDirectory() throws Exception {
        int port; try (var socket = new ServerSocket(0)) { port = socket.getLocalPort(); }
        var blog = create(port);
        try {
            blog.start();
            assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                while (!blog.status().get("state").equals("RUNNING")) Thread.sleep(25);
            });
            assertThat(blog.status()).containsEntry("canOpen", true).containsEntry("canStart", false).containsEntry("canStop", true);
            assertThatThrownBy(blog::start).isInstanceOf(IllegalArgumentException.class);
            long pid = ((Number) blog.status().get("pid")).longValue();
            assertThat(blog.logs().get("body")).contains("Blog ready");
            assertThat(blog.logs().get("body")).contains("Knowledge synced");
            assertThat(blog.status()).containsEntry("knowledgeState", "SUCCESS");
            blog.stop(); blog.stop();
            assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                while (!blog.status().get("state").equals("STOPPED")) Thread.sleep(25);
            });
            assertThat(ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false)).isFalse();
            assertThat(blog.status()).containsEntry("canOpen", false).containsEntry("canStart", true);
        } finally { blog.shutdown(); }
    }

    @Test void preparesFirstPageAndRetriesHttpErrorsBeforeOpeningPreview() throws Exception {
        int port; try (var socket = new ServerSocket(0)) { port = socket.getLocalPort(); }
        var blog = create(port);
        Path allowPage = root.resolve("front end/KumaBlog/ready");
        Files.writeString(root.resolve("front end/KumaBlog/node_modules/astro/astro.js"), """
                const http=require('node:http'),fs=require('node:fs');
                const port=Number(process.argv[process.argv.indexOf('--port')+1]);
                http.createServer((req,res)=>{
                    console.log('Page requested');
                    if(req.headers.upgrade) { res.writeHead(400); res.end('HTTP/1.1 required'); return; }
                    if(!fs.existsSync('ready')) { res.writeHead(503); res.end('Compiling'); return; }
                    setTimeout(()=>res.end('Blog test'),300);
                }).listen(port,'127.0.0.1');
                """);
        try {
            blog.start();
            assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                while (!blog.logs().get("body").contains("Page requested")) { blog.status(); Thread.sleep(25); }
            });
            assertThat(blog.status()).containsEntry("state", "STARTING").containsEntry("canOpen", false).containsEntry("canStop", true);
            Files.writeString(allowPage, "ready");
            assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                while (!Boolean.TRUE.equals(blog.status().get("canOpen"))) Thread.sleep(25);
            });
            assertThat(blog.status()).containsEntry("state", "RUNNING");
        } finally { blog.shutdown(); }
    }

    @Test void matchesOnlyAstroDevFromConfiguredDirectory() throws Exception {
        var blog = create(4321);
        String cli = root.resolve("front end/KumaBlog/node_modules/astro/astro.js").toString();
        assertThat(blog.matches("node.exe", "node \"" + cli + "\" dev")).isTrue();
        String pnpmCli = root.resolve("front end/KumaBlog/node_modules/.bin/../astro/astro.js").toString();
        assertThat(blog.matches("node.exe", "node   \"" + pnpmCli + "\" dev")).isTrue();
        assertThat(blog.matches("node.exe", "node \"" + cli + "\" build")).isFalse();
        assertThat(blog.matches("node.exe", "node D:/other/node_modules/astro/astro.js dev")).isFalse();
        assertThat(blog.matches("java.exe", "node \"" + cli + "\" dev")).isFalse();
    }

    @Test void refusesToStartOrStopAnUnrelatedPortOwner() throws Exception {
        try (var socket = new ServerSocket(0)) {
            var blog = create(socket.getLocalPort());
            assertThat(blog.status()).containsEntry("state", "EXTERNAL").containsEntry("canStart", false).containsEntry("canStop", false).containsEntry("canOpen", false);
            assertThatThrownBy(blog::start).isInstanceOf(IllegalArgumentException.class);
            blog.stop();
            assertThat(socket.isClosed()).isFalse();
        }
    }

    @Test void syncFailurePreventsFrontendLaunch() throws Exception {
        var blog = create(4322);
        Files.writeString(root.resolve("front end/KumaBlog/scripts/sync-knowledge.mjs"), "console.error('Bad knowledge build');process.exit(7);");
        try {
            blog.start();
            assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                while (!blog.status().get("knowledgeState").equals("FAILED")) Thread.sleep(25);
            });
            assertThat(blog.status()).containsEntry("state", "FAILED").containsEntry("pid", 0L).containsEntry("canStart", true);
            assertThat(blog.logs().get("body")).contains("Bad knowledge build");
        } finally { blog.shutdown(); }
    }

    @Test void manualSyncDoesNotStartBlogAndCanBeCancelled() throws Exception {
        var blog = create(4322);
        try {
            blog.syncKnowledge();
            assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                while (!blog.status().get("knowledgeState").equals("SUCCESS")) Thread.sleep(25);
            });
            assertThat(blog.status()).containsEntry("state", "STOPPED").containsEntry("pid", 0L);
            Files.writeString(root.resolve("front end/KumaBlog/scripts/sync-knowledge.mjs"), "console.log('Sync running');setInterval(()=>{},1000);");
            blog.syncKnowledge();
            assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                while (((Number)blog.status().get("knowledgePid")).longValue()==0) Thread.sleep(25);
            });
            long syncPid = ((Number)blog.status().get("knowledgePid")).longValue();
            assertThat(blog.status()).containsEntry("canStart", false).containsEntry("canSync", false).containsEntry("canStop", true);
            assertThatThrownBy(blog::syncKnowledge).isInstanceOf(IllegalArgumentException.class);
            blog.stop();
            assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                while (!blog.status().get("state").equals("STOPPED")) Thread.sleep(25);
            });
            assertThat(blog.status()).containsEntry("knowledgeState", "CANCELLED");
            assertThat(ProcessHandle.of(syncPid).map(ProcessHandle::isAlive).orElse(false)).isFalse();
        } finally { blog.shutdown(); }
    }

    @Test void stopClosesFrontendAndActiveKnowledgeBuildIncludingChildren() throws Exception {
        int port; try (var socket = new ServerSocket(0)) { port = socket.getLocalPort(); }
        var blog = create(port);
        try {
            blog.start();
            assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                while (!blog.status().get("state").equals("RUNNING")) Thread.sleep(25);
            });
            long frontendPid = ((Number)blog.status().get("pid")).longValue();
            Files.writeString(root.resolve("front end/KumaBlog/scripts/sync-knowledge.mjs"), """
                    import {spawn} from 'node:child_process';
                    spawn(process.execPath,['-e','setInterval(()=>{},1000)'],{stdio:'ignore'});
                    setInterval(()=>{},1000);
                    """);
            blog.syncKnowledge();
            assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                while (((Number)blog.status().get("knowledgePid")).longValue()==0 ||
                        ProcessHandle.of(((Number)blog.status().get("knowledgePid")).longValue()).orElseThrow().descendants().findAny().isEmpty()) Thread.sleep(25);
            });
            long buildPid = ((Number)blog.status().get("knowledgePid")).longValue();
            var children = ProcessHandle.of(buildPid).orElseThrow().descendants().toList();
            blog.stop();
            assertTimeoutPreemptively(Duration.ofSeconds(15), () -> {
                while (!blog.status().get("state").equals("STOPPED") || children.stream().anyMatch(ProcessHandle::isAlive)) Thread.sleep(25);
            });
            assertThat(ProcessHandle.of(frontendPid).map(ProcessHandle::isAlive).orElse(false)).isFalse();
            assertThat(ProcessHandle.of(buildPid).map(ProcessHandle::isAlive).orElse(false)).isFalse();
            assertThat(blog.status()).containsEntry("canOpen", false).containsEntry("knowledgeState", "CANCELLED");
        } finally { blog.shutdown(); }
    }
}
