:-dynamic you_have/1, location/2, open_/1, ducks_eaten/1.
% % Your program goes here
location(egg, duck_pen).
location(ducks, duck_pen).
location(fox, woods).
location(you, house).

connect(duck_pen, yard).
connect(yard, house).
connect(yard, woods).
connect(X,X).
connect(X,Y) :- 
    X = Z,
    connect(Y,Z),
    !.

ducks_eaten(0).

verifica(X) :-
    dif(X,duck_pen).
verifica(X) :-
    X==duck_pen,
    open_(gate),
    location(you, L),
    connect(L, X),
    retract(location(ducks, duck_pen)),
    assert(location(ducks,yard)),
    write(" The ducks have run into the yard"),
    nl.

goto(X) :-
    verifica(X),
    location(you, L),
    connect(L, X),
    retract(location(you, L)),
    assert(location(you,X)),
    write(" You are in the "), write(X),
    nl.
	    
goto(X) :-
    location(you, L),
    dif(X,L),
    write("you're in "), write(L),
    write(" you can't go to "), write(X),nl.

take(X) :-
    location(you, duck_pen),
    X==egg,
    assert(you_have(X)),
    retract(location(egg, duck_pen)),
    write(" You now have the egg"),nl.

open(X) :-
    location(you, yard),
    X==gate,
    assert(open_(X)),
    write("open - "), write(X),nl.

fox_eats:-
    ducks_eaten(N),
    retract(ducks_eaten(N)),
    L is N+1,
    assert(ducks_eaten(L)).

fox :-
    location(ducks, yard),
    location(you, house),
    write("The fox has taken a duck"), nl,
    fox_eats.
    

goto_fox(X) :-
    location(fox, L),
    connect(L, X),
    retract(location(fox, L)),
    assert(location(fox,X)),
    write(" fox is in the "), writeln(X).

move_fox(N):-
    N == 0,
    goto_fox(woods).
move_fox(N):-
    N == 1,
    goto_fox(yard).
move_fox(N):-
    N == 2,
    goto_fox(house).
move_fox(N):-
    N == 3,
    goto_fox(house).


go :-done.
go :-
    write(">>"),
    read(X),
    call(X),
    fox, 
    random(0, 10, N),
    writeln(N),
    move_fox(N).


done :-
    location(you, house),
    you_have(egg),
    write("Thanks for getting the egg."), nl.

reinicializa:-
    writeln("reinicializar-----"),
    retract(you_have(egg)),
    retract(open_(gate)),
    retract(location(ducks, yard)),
    assert(location(ducks, duck_pen)),
	assert(location(egg, duck_pen)),
    location(you, L),
    
    retract(location(you, L)),
    retract(location(you, house)),
    location(fox, D),
    retract(location(fox, D)),
    assert(location(fox, woods)),
    ducks_eaten(N),
	retract(ducks_eaten(N)),
    assert(ducks_eaten(0)),
    you_have(I),
    write(I),
    location(ducks, Q),
    write(Q),
    location(egg, J),
    write(J),
    location(you, K),
    write(K),
    location(fox, W),
    write(W),
    ducks_eaten(H),
    write(H),
    writeln("reinicializar-----").