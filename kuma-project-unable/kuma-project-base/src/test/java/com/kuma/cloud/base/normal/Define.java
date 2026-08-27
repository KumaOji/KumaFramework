package com.kuma.cloud.base.normal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/**
 * Java 常用类型实例化速查
 *
 * @author 曾帅
 * @version 1.0
 */
@SuppressWarnings("all")
public class Define {
    public static void main(String[] args) {

        // ---------------------------------------------------------------- //
        //  基本类型（primitive）                                              //
        // ---------------------------------------------------------------- //
        byte    b    = 1;
        short   s    = 1;
        int     i    = 1;
        long    l    = 1L;
        float   f    = 1.0F;
        double  d    = 1.0D;
        boolean bool = true;
        char    c    = 'a';

        // ---------------------------------------------------------------- //
        //  包装类（Wrapper）                                                  //
        // ---------------------------------------------------------------- //
        Byte      byteObj   = Byte.valueOf((byte) 1);
        Short     shortObj  = Short.valueOf((short) 1);
        Integer   intObj    = Integer.valueOf(1);           // 缓存 [-128, 127]
        Long      longObj   = Long.valueOf(1L);
        Float     floatObj  = Float.valueOf(1.0F);
        Double    doubleObj = Double.valueOf(1.0D);
        Boolean   boolObj   = Boolean.TRUE;
        Character charObj   = Character.valueOf('a');

        // autoboxing（编译器自动装箱）
        Integer autoBoxed = 42;

        // String → 基本类型
        int    parsed  = Integer.parseInt("42");
        long   parsedL = Long.parseLong("100");
        double parsedD = Double.parseDouble("3.14");

        // ---------------------------------------------------------------- //
        //  String                                                           //
        // ---------------------------------------------------------------- //
        String s1 = "hello";                             // 字面量（字符串常量池）
        String s2 = new String("hello");                 // 堆对象（不推荐）
        String s3 = String.valueOf(123);                 // 其他类型转 String
        String s4 = String.format("key=%s val=%d", "x", 1);
        String s5 = new String(new char[]{'h','i'});     // char[] 构造
        String s6 = """
                text block
                """;                                     // Text Block (Java 15+)

        StringBuilder sb  = new StringBuilder();
        StringBuilder sb2 = new StringBuilder("init");
        StringBuilder sb3 = new StringBuilder(64);      // 指定初始容量
        StringBuffer  sbf = new StringBuffer();          // 线程安全版

        // ---------------------------------------------------------------- //
        //  数组（Array）                                                      //
        // ---------------------------------------------------------------- //
        boolean[] booleans  = {true, false};
        byte[]    bytes     = {1, 2, 3};
        short[]   shorts    = {1, 2};
        int[]     ints      = {1, 2};
        long[]    longs     = {1L, 2L};
        float[]   floats    = {1.1F, 1.2F};
        double[]  doubles   = {1.1D, 1.2D};
        char[]    chars     = {'a', 'b'};
        String[]  strings   = {"a", "b"};

        int[]   zeroArr = new int[5];                    // 默认填充 0/false/null
        int[][] matrix  = {{1, 2}, {3, 4}};              // 二维数组
        int[]   copied  = Arrays.copyOf(ints, ints.length);

        // ---------------------------------------------------------------- //
        //  大数（BigInteger / BigDecimal）                                    //
        // ---------------------------------------------------------------- //
        BigInteger bi1 = BigInteger.valueOf(123);
        BigInteger bi2 = new BigInteger("123456789012345678901234567890");
        BigInteger bi3 = BigInteger.ZERO;                // 常量 0 / 1 / TWO / TEN

        BigDecimal bd1 = BigDecimal.valueOf(123);
        BigDecimal bd2 = new BigDecimal("123.456");      // 推荐 String 构造，避免浮点精度问题
        BigDecimal bd3 = new BigDecimal(123);            // int 构造
        BigDecimal bd4 = BigDecimal.ZERO;

        // ---------------------------------------------------------------- //
        //  List                                                             //
        // ---------------------------------------------------------------- //
        List<Integer> listImmutable  = List.of(1, 2, 3);               // 不可变
        List<Integer> listArrayList  = new ArrayList<>(List.of(1, 2)); // 可变，默认容量 10
        List<Integer> listWithCap    = new ArrayList<>(32);            // 预设容量，避免扩容
        List<Integer> listLinked     = new LinkedList<>();              // 频繁插删用
        List<Integer> listFixed      = Arrays.asList(1, 2, 3);         // 固定大小（可 set，不可增删）
        List<Integer> listSingleton  = Collections.singletonList(1);   // 不可变单元素

        // ---------------------------------------------------------------- //
        //  Set                                                              //
        // ---------------------------------------------------------------- //
        Set<String> setImmutable  = Set.of("a", "b", "c");             // 不可变，无序
        Set<String> setHash       = new HashSet<>(Set.of("a", "b"));   // 可变，无序
        Set<String> setLinked     = new LinkedHashSet<>();              // 保持插入顺序
        Set<String> setTree       = new TreeSet<>();                    // 自然排序（SortedSet）
        Set<String> setSingleton  = Collections.singleton("x");        // 不可变单元素

        // ---------------------------------------------------------------- //
        //  Map                                                              //
        // ---------------------------------------------------------------- //
        Map<String, Integer> mapImmutable = Map.of("k1", 1, "k2", 2);  // 不可变
        Map<String, Integer> mapEntries   = Map.ofEntries(             // 超过 10 对时使用
                Map.entry("k1", 1), Map.entry("k2", 2));
        Map<String, Integer> mapHash      = new HashMap<>();            // 无序
        Map<String, Integer> mapLinked    = new LinkedHashMap<>();      // 保持插入顺序
        Map<String, Integer> mapTree      = new TreeMap<>();            // Key 自然排序
        Map<String, Integer> mapSingleton = Collections.singletonMap("k", 1);

        // ---------------------------------------------------------------- //
        //  Queue / Deque / Stack                                            //
        // ---------------------------------------------------------------- //
        Queue<Integer>  queueLinked   = new LinkedList<>();             // FIFO 队列
        Queue<Integer>  queueArray    = new ArrayDeque<>();             // 性能优于 LinkedList
        Queue<Integer>  queuePriority = new PriorityQueue<>();          // 小顶堆（自然排序）
        Queue<Integer>  queueMaxHeap  = new PriorityQueue<>(Comparator.reverseOrder()); // 大顶堆

        Deque<Integer>  deque         = new ArrayDeque<>();             // 双端队列 / 栈首选
        Deque<Integer>  dequeLinked   = new LinkedList<>();

        // Deque 用作栈（替代 java.util.Stack）
        Deque<Integer>  stack         = new ArrayDeque<>();
        stack.push(1);                                                  // 入栈 = addFirst
        stack.pop();                                                    // 出栈 = removeFirst

        // ---------------------------------------------------------------- //
        //  Stream                                                           //
        // ---------------------------------------------------------------- //
        Stream<Integer>  streamOf      = Stream.of(1, 2, 3);
        Stream<Integer>  streamEmpty   = Stream.empty();
        Stream<Integer>  streamIterate = Stream.iterate(0, n -> n + 1); // 无限流，需 limit
        Stream<String>   streamGen     = Stream.generate(() -> "x");    // 无限流，需 limit
        Stream<Integer>  streamList    = listArrayList.stream();
        Stream<Integer>  streamParallel= listArrayList.parallelStream(); // 并行流

        IntStream    intStream    = IntStream.range(0, 10);             // [0, 10)
        IntStream    intRangeClosed = IntStream.rangeClosed(1, 10);     // [1, 10]
        IntStream    intArray     = Arrays.stream(ints);
        LongStream   longStream   = LongStream.of(1L, 2L);
        DoubleStream doubleStream = DoubleStream.of(1.0, 2.0);

        // ---------------------------------------------------------------- //
        //  Optional                                                         //
        // ---------------------------------------------------------------- //
        Optional<String> optOf       = Optional.of("value");           // 值不能为 null
        Optional<String> optEmpty    = Optional.empty();
        Optional<String> optNullable = Optional.ofNullable(null);      // 允许 null

        // ---------------------------------------------------------------- //
        //  日期 / 时间（java.time，Java 8+）                                  //
        // ---------------------------------------------------------------- //
        LocalDate      today    = LocalDate.now();
        LocalDate      date     = LocalDate.of(2024, 1, 1);
        LocalDate      dateParse= LocalDate.parse("2024-01-01");

        LocalTime      timeNow  = LocalTime.now();
        LocalTime      time2    = LocalTime.of(10, 30, 0);

        LocalDateTime  ldtNow   = LocalDateTime.now();
        LocalDateTime  ldt      = LocalDateTime.of(2024, 1, 1, 10, 30);
        LocalDateTime  ldtParse = LocalDateTime.parse("2024-01-01T10:30:00");

        ZonedDateTime  zdt      = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        Instant        instant  = Instant.now();
        Duration       duration = Duration.ofHours(1);                 // 时间段（秒级）
        Period         period   = Period.ofDays(7);                    // 日期段（天级）

        // ---------------------------------------------------------------- //
        //  原子类（java.util.concurrent.atomic）                              //
        // ---------------------------------------------------------------- //
        AtomicInteger            atomicInt  = new AtomicInteger(0);
        AtomicLong               atomicLong = new AtomicLong(0L);
        AtomicBoolean            atomicBool = new AtomicBoolean(false);
        AtomicReference<String>  atomicRef  = new AtomicReference<>("init");
    }
}
