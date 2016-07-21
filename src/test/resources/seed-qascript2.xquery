xquery version "1.0";


let $numbers := (1,2,3,4)


for $n in $numbers 
return
	
	<number>{data($n)}</number>
