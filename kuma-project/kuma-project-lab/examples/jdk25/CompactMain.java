// JDK 25: compact source file + instance main + java.lang.IO (all permanent).
void main() {
    var values = List.of(1, 2, 3); // java.base types are implicitly imported in compact source files
    int total = values.stream().mapToInt(Integer::intValue).sum();
    if (total != 6) throw new IllegalStateException("compact main sum failed");
    IO.println("JDK25 compact source / instance main / IO.println: sum=6 passed.");
}
