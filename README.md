#CheckDigitCLI
A general utility CLI for dealing with check-digits.

##Commands
| Command | Aliases | Function | Options |
| --- | --- | --- | --- |
| `cdcli` | none | Root command. Directs user to the `help` command. | none | 
| `help` 	| none | Provides the copyright notice, author name, and link to GitHub repository. | none |
| `exit`	| `quit`, `terminate` | Halts the program. (console window remains open) | none |
| `generateCheckDigits` | `gcd` | Given a list of alphanumeric payloads, generates a list of corresponding check-digits | `-a/-algorithm, eg. -a=luhn (required)` specifies the check-digits algorithm to use. `-if/-inputFile, eg. -if=in.txt` is a file in which payloads can be found. `-of/-outputFile, eg. -of=out.txt` is a file to print the results of the command to; if set, will only print to file, not to the console. `-p/-payload, eg. -p=1234` adds a payload; can be chained, for example `-p=1234 -p=5678`. |