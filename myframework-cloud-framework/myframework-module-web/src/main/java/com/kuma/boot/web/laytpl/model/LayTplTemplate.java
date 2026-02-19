/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.support.function.CheckedFunction
 *  com.kuma.boot.common.support.function.Unchecked
 *  com.kuma.boot.common.utils.collection.CollectionUtils
 *  com.kuma.boot.common.utils.io.IoUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.core.io.Resource
 */
package com.kuma.boot.web.laytpl.model;

import com.kuma.boot.common.support.function.CheckedFunction;
import com.kuma.boot.common.support.function.Unchecked;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.io.IoUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.laytpl.exception.LayTplException;
import com.kuma.boot.web.laytpl.js.JsConsole;
import com.kuma.boot.web.laytpl.js.JsContext;
import com.kuma.boot.web.laytpl.properties.LayTplProperties;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

public class LayTplTemplate
implements ApplicationContextAware,
InitializingBean {
    private final ConcurrentMap<String, String> tplCache = new ConcurrentHashMap<String, String>();
    private final Function<String, String> tplFunction = Unchecked.function((CheckedFunction & Serializable)tpl -> {
        Resource resource = this.getApplicationContext().getResource(tpl);
        return IoUtils.readToString((InputStream)resource.getInputStream());
    });
    private final LayTplProperties tplProperties;
    private final JsConsole console;
    private final FmtFunc fmtFunc;
    private ApplicationContext applicationContext;
    private ScriptEngine engine;

    public LayTplTemplate(LayTplProperties tplProperties, FmtFunc fmtFunc) {
        this.tplProperties = tplProperties;
        this.fmtFunc = fmtFunc;
        this.console = new JsConsole();
        try {
            ScriptEngineManager engineManager = new ScriptEngineManager();
            ScriptEngine engine = engineManager.getEngineByMimeType("text/javascript");
            Bindings bindings = engine.createBindings();
            HashMap<String, String> config = new HashMap<String, String>(4);
            config.put("open", tplProperties.getOpen());
            config.put("close", tplProperties.getClose());
            bindings.put("console", (Object)this.console);
            bindings.put("fmt", (Object)fmtFunc);
            bindings.put("cloud", (Object)new JsContext(this.applicationContext));
            bindings.put("_config", (Object)config);
            engine.setBindings(bindings, 200);
            engine.eval("var window = {};\n\nvar config = {\n  open: _config.open,\n  close: _config.close\n};\n\nvar tool = {\n    exp: function (str) {\n        return new RegExp(str, 'g');\n    },\n    //\u5339\u914d\u6ee1\u8db3\u89c4\u5219\u5185\u5bb9\n    query: function (type, _, __) {\n        var types = [\n            '#([\\\\s\\\\S])+?',   //js\u8bed\u53e5\n            '([^{#}])*?' //\u666e\u901a\u5b57\u6bb5\n        ][type || 0];\n        return exp((_ || '') + config.open + types + config.close + (__ || ''));\n    },\n    escape: function (html) {\n        return String(html || '').replace(/&(?!#?[a-zA-Z0-9]+;)/g, '&amp;')\n            .replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/'/g, '&#39;').replace(/\"/g, '&quot;');\n    },\n    error: function (e, tplog) {\n        var error = 'Laytpl Error\uff1a';\n        typeof console === 'object' && console.error(error + e + '\\n' + (tplog || ''));\n        return error + e;\n    }\n};\n\nvar exp = tool.exp, Tpl = function (tpl) {\n    this.tpl = tpl;\n};\n\nTpl.pt = Tpl.prototype;\n\nwindow.errors = 0;\n\n//\u7f16\u8bd1\u6a21\u7248\nTpl.pt.parse = function (tpl, data) {\n    var that = this, tplog = tpl;\n    var jss = exp('^' + config.open + '#', ''), jsse = exp(config.close + '$', '');\n\n    tpl = tpl.replace(/\\s+|\\r|\\t|\\n/g, ' ')\n        .replace(exp(config.open + '#'), config.open + '# ')\n        .replace(exp(config.close + '}'), '} ' + config.close).replace(/\\\\/g, '\\\\\\\\')\n        //\u4e0d\u5339\u914d\u6307\u5b9a\u533a\u57df\u7684\u5185\u5bb9\n        .replace(exp(config.open + '!(.+?)!' + config.close), function (str) {\n            str = str.replace(exp('^' + config.open + '!'), '')\n                .replace(exp('!' + config.close), '')\n                .replace(exp(config.open + '|' + config.close), function (tag) {\n                    return tag.replace(/(.)/g, '\\\\$1')\n                });\n            return str\n        })\n        //\u5339\u914dJS\u89c4\u5219\u5185\u5bb9\n        .replace(/(?=\"|')/g, '\\\\').replace(tool.query(), function (str) {\n            str = str.replace(jss, '').replace(jsse, '');\n            return '\";' + str.replace(/\\\\/g, '') + ';view+=\"';\n        })\n        //\u5339\u914d\u666e\u901a\u5b57\u6bb5\n        .replace(tool.query(1), function (str) {\n            var start = '\"+(';\n            if (str.replace(/\\s/g, '') === config.open + config.close) {\n                return '';\n            }\n            str = str.replace(exp(config.open + '|' + config.close), '');\n            if (/^=/.test(str)) {\n                str = str.replace(/^=/, '');\n                start = '\"+_escape_(';\n            }\n            return start + str.replace(/\\\\/g, '') + ')+\"';\n        });\n\n    tpl = '\"use strict\";var view = \"' + tpl + '\";return view;';\n\n    try {\n        that.cache = tpl = new Function('d, _escape_', tpl);\n        return tpl(data, tool.escape);\n    } catch (e) {\n        delete that.cache;\n        return tool.error(e, tplog);\n    }\n};\n\nTpl.pt.render = function (data, callback) {\n    var that = this, tpl;\n    if (!data) return tool.error('no data');\n    tpl = that.cache ? that.cache(data, tool.escape) : that.parse(that.tpl, data);\n    if (!callback) return tpl;\n    callback(tpl);\n};\n\nvar laytpl = function (tpl) {\n    if (typeof tpl !== 'string') return tool.error('Template not found');\n    return new Tpl(tpl);\n};\n\nlaytpl.config = function (options) {\n    options = options || {};\n    for (var i in options) {\n        config[i] = options[i];\n    }\n};\n\nlaytpl.v = '1.2.0';\n", bindings);
            this.engine = engine;
            this.engine.eval("console.log('MicaTpl init, laytpl version:{}', laytpl.v);");
        }
        catch (ScriptException e) {
            LogUtils.error((Throwable)e);
        }
    }

    public String renderTpl(String tplName, Object data) {
        if (tplName.startsWith("/")) {
            tplName = tplName.substring(1);
        }
        String tplPath = this.tplProperties.getPrefix() + tplName;
        try {
            String html = this.tplProperties.isCache() ? (String)CollectionUtils.computeIfAbsent(this.tplCache, (Object)tplPath, this.tplFunction) : this.tplFunction.apply(tplPath);
            return this.renderHtml(html, data);
        }
        catch (ScriptException e) {
            throw new LayTplException(e);
        }
    }

    public String render(String html) {
        return this.render(html, new HashMap(0));
    }

    public String render(String html, Object data) {
        try {
            return this.renderHtml(html, data);
        }
        catch (ScriptException e) {
            throw new LayTplException(e);
        }
    }

    private String renderHtml(String html, Object data) throws ScriptException {
        Bindings bindings = this.engine.createBindings();
        bindings.put("_html_", (Object)html);
        bindings.put("data", data);
        return (String)this.engine.eval("laytpl(_html_).render(data);", bindings);
    }

    public void afterPropertiesSet() throws Exception {
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }
}

