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
$i = input : integer
print $i; " * "; $i; " = "; $i * $i; 10
```
```
# Some explicit typings
$a : number             # = 0
$b : string             # = ""
$c : string = $b + $a   # = "0"
```
```
# Some type casts
$a = input : integer
$b = input : string
println (string)$a + $b
```
```
# Some if/else statements
$a = input : double
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
println 2 + (integer)(3.14 + (double)true)
```