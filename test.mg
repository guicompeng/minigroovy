def a = read('Entre com um valor: ') as Integer
println('Voce entrou com ' + a)

if(a > 5)
    println('a eh maior que 5')
else if(a == 5)
    println('oba, 5 eh igual a 5')
else
    println('a eh menor que 5')

for(def b = 0, c = 0; b < 10; b += 1, c += 2) {
    println('b: ' + b + ' - c: ' + c)
}