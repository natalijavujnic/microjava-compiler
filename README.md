# MicroJava Compiler

A Java-based compiler for **MicroJava**, developed as a university compiler-construction project. The compiler processes MicroJava source code through lexical and syntax analysis, semantic validation, and bytecode generation for the MicroJava virtual machine.

## Overview

The implementation follows a conventional compilation pipeline:

1. **Lexical analysis** — a JFlex lexer recognizes keywords, identifiers, literals, operators, and punctuation.
2. **Syntax analysis** — a CUP grammar parses tokens and constructs an abstract syntax tree (AST).
3. **Semantic analysis** — an AST visitor performs symbol-table management, scope resolution, type checking, and other language-specific validations.
4. **Code generation** — another AST visitor emits MicroJava VM instructions and writes the resulting object file.

## Features

- Declarations of constants, variables, arrays, and methods
- Primitive types, including `int`, `char`, and `bool`
- Arithmetic, relational, and logical expressions
- Assignment, method calls, input/output, and return statements
- Conditional statements and `do-while` loops, including `break` and `continue`
- Semantic checks for declarations, types, assignments, method arguments, and return statements
- Support for the `set` type and set-related operations
- Code generation for the MicroJava virtual machine

The language grammar also includes constructs such as `map`, `union`, and `intersection`. See the grammar and implementation for their exact syntax and semantics.

## Technologies

- **Java** — compiler implementation
- **JFlex** — lexical analyzer generation
- **Java CUP** — parser and AST generation
- **Apache Ant** — build automation
- **Log4j** — compiler diagnostics and logging
- **MicroJava runtime and symbol-table libraries** — bytecode support and semantic infrastructure

## Project Structure

```text
MJCompiler/
├── src/                  # Compiler implementation and AST classes
│   └── rs/ac/bg/etf/pp1/
│       ├── SemanticAnalyzer.java
│       ├── CodeGenerator.java
│       ├── MJParser.java
│       ├── Yylex.java
│       └── ast/
├── spec/
│   ├── mjlexer.lex       # JFlex lexer specification
│   └── mjparser.cup      # CUP grammar specification
├── test/                 # Sample MicroJava programs and test drivers
├── lib/                  # Project dependencies
├── config/               # Logging configuration
└── build.xml             # Ant build targets
```

Some lexer, parser, and AST Java sources are generated from the specifications in `spec/`.

## Implementation Highlights

### Semantic Analysis

`SemanticAnalyzer.java` uses an AST visitor and a symbol table to validate declarations and references, variable and method scopes, type compatibility, function parameters, return statements, and valid usage of loop-control statements. Diagnostics include source-line information where available.

### Bytecode Generation

`CodeGenerator.java` emits MicroJava VM instructions for expressions, method calls, branching, loops, input/output, and other supported constructs. It manages jump-address fixups for control flow and implements built-in operations, including set-related routines.

## Notes

This repository preserves the university project structure, including generated Java sources, Eclipse configuration, and local JAR dependencies. Some AST files have numbered filename variants; these should be reviewed before treating the repository as a cleaned-up release.

## Author

**Natalija Vujnic** — [GitHub profile](https://github.com/natalijavujnic)
