#define _GNU_SOURCE
#include <errno.h>
#include <inttypes.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/mman.h>
#include <sys/resource.h>
#include <sys/syscall.h>
#include <time.h>
#include <unistd.h>

/* Linux-only teaching experiments. No root, kernel settings or existing files needed. */
static void require(int ok, const char *operation) {
    if (!ok) { perror(operation); exit(EXIT_FAILURE); }
}

static double seconds(struct timeval value) {
    return (double)value.tv_sec + (double)value.tv_usec / 1000000.0;
}

static void cpu_experiment(int use_syscall) {
    struct rusage before, after;
    struct timespec start, end;
    volatile uint64_t sink = 1;
    const int count = 1000000;
    require(getrusage(RUSAGE_SELF, &before) == 0, "getrusage before");
    require(clock_gettime(CLOCK_MONOTONIC, &start) == 0, "clock_gettime");
    for (int i = 0; i < count; i++) {
        if (use_syscall) {
            /* Explicit syscall avoids library caches/vDSO paths hiding transitions. */
            long pid = syscall(SYS_getpid);
            require(pid > 0, "SYS_getpid");
            sink += (uint64_t)pid;
        } else {
            sink = sink * 1664525 + 1013904223;
        }
    }
    require(clock_gettime(CLOCK_MONOTONIC, &end) == 0, "clock_gettime");
    require(getrusage(RUSAGE_SELF, &after) == 0, "getrusage after");
    double wall = (double)(end.tv_sec - start.tv_sec) + (double)(end.tv_nsec - start.tv_nsec) / 1e9;
    printf("%s: iterations=%d wall=%.6fs user=%.6fs system=%.6fs voluntary_cs=%ld involuntary_cs=%ld sink=%" PRIu64 "\n",
           use_syscall ? "explicit SYS_getpid" : "user arithmetic", count, wall,
           seconds(after.ru_utime) - seconds(before.ru_utime),
           seconds(after.ru_stime) - seconds(before.ru_stime),
           after.ru_nvcsw - before.ru_nvcsw, after.ru_nivcsw - before.ru_nivcsw, sink);
    puts("A privilege transition is not necessarily a scheduler context switch. Timing is observation, not a speed assertion.");
}

static void show_maps(void) {
    FILE *maps = fopen("/proc/self/maps", "r");
    require(maps != NULL, "open maps");
    char line[1024];
    puts("/proc/self/maps: user virtual address mappings (not kernel physical addresses)");
    while (fgets(line, sizeof(line), maps)) fputs(line, stdout);
    require(!ferror(maps), "read maps");
    require(fclose(maps) == 0, "close maps");
}

static void memory_experiment(void) {
    long page = sysconf(_SC_PAGESIZE);
    require(page > 0, "page size");
    size_t size = (size_t)page * 256;
    unsigned char *anonymous = mmap(NULL, size, PROT_READ | PROT_WRITE,
                                   MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
    require(anonymous != MAP_FAILED, "anonymous mmap");
    struct rusage before, after;
    require(getrusage(RUSAGE_SELF, &before) == 0, "getrusage");
    for (size_t i = 0; i < size; i += (size_t)page) anonymous[i] = 42;
    require(getrusage(RUSAGE_SELF, &after) == 0, "getrusage");
    printf("Anonymous mmap %p size=%zu touched pages=256 minor_fault_delta=%ld\n",
           (void *)anonymous, size, after.ru_minflt - before.ru_minflt);

    /* tmpfile owns a new anonymous temporary file; no user's file is overwritten. */
    FILE *file = tmpfile();
    require(file != NULL, "tmpfile");
    int fd = fileno(file);
    require(fd >= 0 && ftruncate(fd, page) == 0, "ftruncate");
    unsigned char *shared = mmap(NULL, (size_t)page, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    require(shared != MAP_FAILED, "shared mmap");
    unsigned char *private = mmap(NULL, (size_t)page, PROT_READ | PROT_WRITE, MAP_PRIVATE, fd, 0);
    require(private != MAP_FAILED, "private mmap");
    shared[0] = 42;
    require(msync(shared, (size_t)page, MS_SYNC) == 0, "msync");
    unsigned char copied = 0;
    require(pread(fd, &copied, 1, 0) == 1, "pread shared");
    require(copied == 42, "MAP_SHARED file content");
    private[0] = 99; /* copy-on-write: changes this mapping rather than the backing file */
    require(pread(fd, &copied, 1, 0) == 1, "pread private");
    require(copied == 42 && shared[0] == 42 && private[0] == 99, "MAP_PRIVATE isolation");
    /* The kernel validates user buffers; a mapped address need not be writable. */
    require(mprotect(anonymous, size, PROT_NONE) == 0, "mprotect none");
    errno = 0;
    ssize_t denied = pread(fd, anonymous, 1, 0);
    require(denied == -1 && errno == EFAULT, "protected user buffer must return EFAULT");
    require(mprotect(anonymous, size, PROT_READ | PROT_WRITE) == 0, "mprotect restore");
    require(pread(fd, anonymous, 1, 0) == 1 && anonymous[0] == 42, "restored user buffer");
    puts("User buffer protection: PROT_NONE -> pread EFAULT; restored read/write -> pread succeeds.");
    printf("file fd=%d shared=%p private=%p pread buffer=%p: shared=42 private=99 file=42\n",
           fd, (void *)shared, (void *)private, (void *)&copied);
    puts("pread requests kernel I/O into a user buffer; mmap exposes a mapping accessed with ordinary loads/stores (faults may enter the kernel).");
    show_maps();
    require(munmap(private, (size_t)page) == 0, "munmap private");
    require(munmap(shared, (size_t)page) == 0, "munmap shared");
    require(fclose(file) == 0, "close temporary file");
    require(munmap(anonymous, size) == 0, "munmap anonymous");
    puts("Linux memory correctness checks passed.");
}

int main(int argc, char **argv) {
    const char *mode = argc == 2 ? argv[1] : "all";
    if (argc > 2 || (strcmp(mode, "all") && strcmp(mode, "cpu") &&
                    strcmp(mode, "syscall") && strcmp(mode, "memory"))) {
        fprintf(stderr, "Usage: %s [all|cpu|syscall|memory]\n", argv[0]);
        return EXIT_FAILURE;
    }
    if (!strcmp(mode, "all") || !strcmp(mode, "cpu")) cpu_experiment(0);
    if (!strcmp(mode, "all") || !strcmp(mode, "syscall")) cpu_experiment(1);
    if (!strcmp(mode, "all") || !strcmp(mode, "memory")) memory_experiment();
    return EXIT_SUCCESS;
}
