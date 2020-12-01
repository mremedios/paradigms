node(Key, Value, Y, L, R).

split(null, K, null, null, null) :- !.

split(node(Key, Value, Y, L, R), K, Left, Middle, node(Key, Value, Y, Right, R)) :-
	K < Key,
	split(L, K, Left, Middle, Right), !.

split(node(K, Value, Y, L, R), K, L, node(K, Value, Y, null, null), R) :-  !.

split(node(Key, Value, Y, L, R), K, node(Key, Value, Y, L, Left), Middle, Right) :-
	split(R, K, Left, Middle, Right).

merge(null, A, A) :- !.

merge(A, null, A) :- !.

merge(node(Key1, Value1, Y1, L1, R1), node(Key2, Value2, Y2, L2, R2), 
			node(Key1, Value1, Y1, L1, A)) :-
	Y1 > Y2,
	merge(R1, node(Key2, Value2, Y2, L2, R2), A), !.

merge(node(Key1, Value1, Y1, L1, R1), node(Key2, Value2, Y2, L2, R2), 
			node(Key2, Value2, Y2, A, R2)) :-
	merge(node(Key1, Value1, Y1, L1, R1), L2, A).

map_put(TreeMap, Key, Value, Result) :-
	split(TreeMap, Key, Left, _, Right),
	rand_int(1000000, Y),
	merge(Left, node(Key, Value, Y, null, null), A),
	merge(A, Right, Result).

map_build([], null) :- !.

map_build([(F, S)| T], TreeMap) :-
	map_build(T, Tree),
	map_put(Tree, F, S, TreeMap).

map_get(TreeMap, Key, Value) :-
	split(TreeMap, Key, _, node(Key, Value, _, _, _), _).

map_remove(TreeMap, Key, Result) :-
	split(TreeMap, Key, Left, _, Right),
	merge(Left, Right, Result).

map_floorKey(Map, Key, FloorKey) :-
	findKey(Map, Key, null, FloorKey).

findKey(null, _, C, C) :- 
	C \= null, !.

findKey(node(Key, Value, Y, L, R), Key, _, Key) :- !.
	
findKey(node(Key, Value, Y, L, R), K, Current, FloorKey) :-
	K < Key,
	findKey(L, K, Current, FloorKey).

findKey(node(Key, Value, Y, L, R), K, Current, FloorKey) :-
	K > Key,
	findKey(R, K, Key, FloorKey).

	