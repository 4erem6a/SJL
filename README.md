# Sample JVM Language
Sample JVM JIT compiler for a sample language created for educational purposes.
# A few examples:
```
# Sample "Hello, world!" program:
println "Hello, world!"
```
```
# Some math & variables:
$x = 10.0
$y = 3.14
println $x * ($y + ($x / -$y))
```
```
# Some user input:
$i = input: @integer
print $i; " * "; $i; " = "; $i * $i; 10
```
```
# Some explicit typings
let $a: @integer            # = 0
let $b: @string             # = ""
let $c: @string = $b + $a   # = "0"
```
```
# Some explicit casts
$a = input: @integer
$b = input: @string
println (@string)$a + $b
```
```
# Some if/else statements
$a = input: @double
if ($a > 3.14)
    println "A"
else if ($a >= 10)
    println "B"
else println "C"
```
```
# Some autocasts
println 2 + 3.14 / true
# Same as
println 2 + (@integer)(3.14 + (@double)true)
```
```
# Some scopes
let $x = 1
{
    let $x = 2
    println $x  # OUTPUT: 2
}
println $x      # OUTPUT: 1
```
```
# Some loops
let $a = 0
while ($a < 10) {
    println $a
    $a = $a + 1
}

let $b = 0
do {
    println $b
    $b = $b + 1
} while ($b < 10)

for (let $c = 0; $c < 10; $c = $c + 1)
    println $c
```
```
# Some arrays
let $initialized = [ 1, 2, 3 ]          # { 1, 2, 3 }
let $empty = [] of @integer             # new int[0]
let $uninitialized = [](10) of @integer # new int[10]

$initialized[1] = 3

println $initialized[$initialized[0]];
        length $uninitialized           # array.length
```