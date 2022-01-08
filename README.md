# CheckDigitCLI
A general utility CLI for dealing with check-digits.

## Commands
| Command | Aliases | Function |
| --- | --- | --- |
| `cdcli` | none | Root command. Directs user to the `help` command. |
| `help` 	| none | Provides the copyright notice, author name, and link to GitHub repository. |
| `exit`	| `quit`, `terminate` | Halts the program. (console window remains open) |
| `generateCheckDigits` | `gcd` | Given a list of alphanumeric payloads, generates a list of corresponding check-digits | 

### generateCheckDigits

 - `-a/-algorithm, eg. -a=luhn (required)` specifies the check-digits algorithm to use. 
 - `-if/-inputFile, eg. -if=in.txt` is a file in which payloads can be found. 
 - `-of/-outputFile, eg. -of=out.txt` is a file to print the results of the command to; if set, will only print to file, not to the console. 
 - `-p/-payload, eg. -p=1234` adds a payload; can be chained, for example `-p=1234 -p=5678`.

Input files should contain one payload per line with no additional formatting, for example:
```
1234
5678
9101112
```

Outputs consist of a list of payload/check-digit pairs, sandwiched between a ==START== and ==END== block, for example:
```
==START==
1234 4
5678 8
9101112 2
==END==
```