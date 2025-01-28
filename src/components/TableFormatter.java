package components;

public interface TableFormatter {
    TableFormatter row(String... cells);
    TableFormatter line(String... segments);
    StringBuilder get();
}
