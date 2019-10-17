:- dynamic you_have/1, location/2, gate/1, ducks_lost/1.

location(egg, duck_pen).
location(ducks, duck_pen).
location(fox, woods).
location(you, house).

connect(yard, duck_pen).
connect(yard, house).
connect(yard, woods).

/* 
Se o connect(A, B) não deu certo de primeira, tenta ver se existe connect(B, A). Se sim, retorna true. Se não, retorna False
*/
connect(X, X).
connect(X, Y):-
	x = Z,
	connect(Y, Z), !.

/* 
Checa se estamos no duck_pen. Se não beleza, se sim, retorna False e vai para segunda instancia de gate_check
*/

gate_check(X):-
	dif(X,duck_pen).

/*
Quando estamos no duck_pen, verifica se o portão tá aberto. Se sim, entramos no duck_pen, e os patos fogem pro jardim
*/

gate_check(X):-
	X == duck_pen,
	open(gate),
	location(you, L),
	connect(L, X),
	retract(location(ducks, duck_pen)),
	assert(location(ducks, yard)),
	write(" The ducks have run into the yard"),
	nl.

/*
Verifica se o lugar que eu quero ir está conectado aonde eu estou agora.
Se sim, remove o fato da minha localização atual, e escreve um novo fato para a minha nova localização
*/

goto(X) :- 
	gate_check(X),
	location(you, L),
	connect(L, X),
	retract(location(you, L)),
	assert(location(you, X)),
	write("Your are in the "), 
	write(X), 
	nl.

/*
Se não for possível me mover, roda a segunda instancia de goto, mostrando que não posso ir para onde eu quero de onde estou no momento.
*/
goto(X) :- 
	write("You can't get "),
	write(X), 
	write(" from here"), 
	nl.

/*
Abre o portão, criando o fato do portão.
Esse fato precisa existir para poder se mover para o duck_pen
*/

open(X):-
	location(you, yard),
	X==gate,
	assert(gate(X)),
	write("Open "), write(X), nl.

/*
Pega o ovo. Remove o ovo do duck_pen, e cria o fato de que nós temos o ovo em nossas mãos
*/

take(X) :- 
	location(you, duck_pen),
	X==egg,
	assert(you_have(X)),
	retract(location(egg, duck_pen)),
	write("You've just taken the egg"), nl.

fox :-	
	location(ducks, yard),
	location(you, house),
	write("The fox has taken a duck"),
	nl. 

/* 
Inicia o jogo.
Se done retornar true, fim de jogo.
Se não, executa segunda instancia
*/

go :- 
	done.

/*
* Escreve >>
* Espera ler uma variável X (alguma query no terminal)
* Executa essa query.
* Executa movimentação da raposa.
*/

go :- 
	write(">> "),
	read(X),
	call(X),
	fox,
	go.

/*
* Se você estiver em casa e tiver o ovo em mãos, fim de jogo
*/

done :- 
	location(you, house),
	you_have(egg),
	write("Thanks for getting the egg"), nl.

/* 
* Necessário fazer:
* reiniciar todas as variáveis quando o jogo acabar
* fazer função da raposa se mexer por ai
* fazer raposa sair comendo galinhas
* portão abre direto se tentar entrar no duck_pen. Fazer usuário ter que abrir o portão pra depois entrar.
*/