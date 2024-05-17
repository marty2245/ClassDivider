# Classdivider

*Classdivider* divides a class of students into groups of a given size, +/- a
given deviation. For example, to divide the example class listed in
`students.lst` into groups of 4 +/- 2 students, run *classdivider* as
follows:
  
```bash
java -jar target/classdivider-0.6.jar -g 4 -d 2 students.lst
```

## Usage

```
Usage: classdivider [-hV] [-d=<deviation>] -g=<groupSize> <studentsFile>
Divide a class of students into groups.
      <studentsFile>   path to file with students data in CSV format
  -d, --deviation=<deviation>
                       permitted difference of number of students in a group
                         and the preferred group size. Defaults to 1.
  -g, --group-size=<groupSize>
                       preferred group size.
  -h, --help           Show this help message and exit.
  -V, --version        Print version information and exit.
```

## Building and running *classdivider*

Because *classdivider* uses two external libraries, one for reading CSV files
and one to make nice command-line interfaces, you need to package the
dependencies when building the program.

On the terminal, run:
  
```bash
mvn clean compile assembly:single
```

This actions creates Jar file `target/classdivider-0.6.jar`.

We created a "Maven action" for this in NetBeans called *build*. So, you can
build this project with all dependencies also via the project context menu
*Run Maven* → *build*.

After building the program, you run it in a terminal. In the terminal, change
directory to the project's root directory and run:

```bash
java -jar target/classdivider-0.6.jar -g 4 -d 2 students.lst
```
