init(N) :- sieve(2, N).

comp_table(1).

next(X) :-
	not comp_table(X), !.

next(X) :-
	X1 is X + 1,
	next(X1), !.
	
sieve(X, N) :-
	X1 is X * X,
	N > X1,
	add(X1, X, N),
	X2 is X + 1,
	next(X2),
	sieve(X2, N), !.

add(Y, A, N) :-
	N >= Y,
	assert(comp_table(Y)),
	Y1 is Y + A,
	add(Y1, A, N), !.

add(Y, A, N) :- N < Y, !.

composite(N) :- comp_table(N).

prime(N) :- not comp_table(N).

prime_divisors(1, []) :- !.
	
prime_divisors(N, [H | T]) :- 
	number (N),
	prime_div(N, [H | T], 2), !.

prime_div(1, [], _) :- !.

prime_div(N, [N], _) :-
	prime (N), !.
	
prime_div(N, [H | T], P) :-
	0 is mod(N, P),
	N1 is div(N, P),
	H is P,
	prime_div(N1, T, P), !.

prime_div(N, [H | T], P) :-
	%not (0 is mod(N, P)),
	P1 is P + 1,
	%next(P1),
	prime_div(N, [H | T], P1), !.

prime_divisors(N, [H | T]) :- 
	%not (number (N)),
	divisors(N, [H | T], 0), !.

divisors(1, [], _) :- !.

divisors(N, [H | T], L) :-
	prime(H),
	divisors(N1, T, H),
	H >= L,
	N is N1 * H, !.

toList(X, A, [X]) :-
	X < A, !.

toList(N, A, [H | T]) :-
	H is mod(N, A),
  N1 is div(N, A),
  toList(N1, A, T), !.

prime_palindrome(N, K) :-
	prime(N),
	toList(N, K, V),
	reverse(V, V).

	