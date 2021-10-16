:- discontiguous simplify/3.
:- discontiguous add/3.

numeral(0).
numeral(s(X)) :- numeral(X).
numeral(X+Y) :- numeral(X), numeral(Y).
numeral(p(X)) :- numeral(X).

add(0,X,X).
add(s(X),Y,s(Z)) :- add(X,Y,Z).

% Exercise 1
/** <examples>
?- add2(s(0)+s(s(0)), s(s(0)), Z).
?- add2(0, s(0)+s(s(0)), Z).
?- add2(s(s(0)), s(0)+s(s(0)), Z).
?- add2(s(0)+s(0), s(0+s(s(0))), Z).
*/

add2(X,Y,Z) :- simplify(X,Y,Z).

simplify(s(X+A),Y,s(Z)) :- simplify(X+A,Y,Z).
simplify(X,s(Y+A),s(Z)) :- simplify(X,Y+A,Z).
simplify(s(X+A),s(Y+B),s(s(Z))) :- simplify(X+A,Y+B,Z).

simplify(X+A,Y,Z) :- add(X,A,R), add2(R,Y,Z).
simplify(X,Y+A,Z) :- add(Y,A,R), add2(X,R,Z).
simplify(X+A,Y+B,Z) :- add(X,A,R1), add(Y,B,R2), add2(R1,R2,Z).

% This moved later to get Exercise 2 to work.
% simplify(X,Y,Z) :- add(X,Y,Z).

% Exercise 2
/** <examples>
?- add2(p(s(0)), s(s(0)), Z).
?- add2(0, s(p(0)), Z).
?- add2(p(0)+s(s(0)),s(s(0)),Z).
?- add2(p(0), p(0)+s(p(0)), Z).
*/

simplify(p(s(X)), Y, Z) :- simplify(X,Y,Z).
simplify(X, p(s(Y)), Z) :- simplify(X,Y,Z).
simplify(s(p(X)),Y,Z) :- simplify(X,Y,Z).
simplify(X, s(p(Y)),Z) :- simplify(X,Y,Z).

add(p(X),Y,p(Z)) :- add(X,Y,Z).

% This was taken from end of Exercise 2
simplify(X,Y,Z) :- add(X,Y,Z).
