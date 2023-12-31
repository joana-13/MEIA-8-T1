:- set_prolog_flag(double_quotes, chars).

% Versão preparada para lidar com regras que contenham negação (nao)
% Metaconhecimento
% Usar base de conhecimento veIculos2.txt
% Explicações como?(how?) e porque não?(whynot?)

:-op(220,xfx,entao).
:-op(35,xfy,se).
:-op(240,fx,regra).
:-op(500,fy,nao).
:-op(600,xfy,e).

:-dynamic justifica/3.


carrega_bc:-
		retractall(facto(_,_)),
		retractall(justifica(_,_,_)),
		write('NOME DA BASE DE CONHECIMENTO (terminar com .)-> '),
		read(NBC),
		consult(NBC).

arranca_motor(String):-
		facto(N,Facto),
		facto_dispara_regras1(Facto, LRegras),
		dispara_regras(N, Facto, LRegras),
		ultimo_facto(N),
		format(string(String), "Foi concluído o facto nº ~w -> ~w",[N,Facto]),!.

facto_dispara_regras1(Facto, LRegras):-
	facto_dispara_regras(Facto, LRegras),
	!.
facto_dispara_regras1(_, []).
% Caso em que o facto não origina o disparo de qualquer regra.

dispara_regras(N, Facto, [ID|LRegras]):-
	regra ID se LHS entao RHS,
	facto_esta_numa_condicao(Facto,LHS),
	% Instancia Facto em LHS
	verifica_condicoes(LHS, LFactos),
	member(N,LFactos),
	concluir(RHS,ID,LFactos),
	!,
	dispara_regras(N, Facto, LRegras).

dispara_regras(N, Facto, [_|LRegras]):-
	dispara_regras(N, Facto, LRegras).

dispara_regras(_, _, []):-!.


facto_esta_numa_condicao(F,[F  e _]).

facto_esta_numa_condicao(F,[avalia(F1)  e _]):- F=..[H,H1|_],F1=..[H,H1|_].

facto_esta_numa_condicao(F,[_ e Fs]):- facto_esta_numa_condicao(F,[Fs]).

facto_esta_numa_condicao(F,[F]).

facto_esta_numa_condicao(F,[avalia(F1)]):-F=..[H,H1|_],F1=..[H,H1|_].


verifica_condicoes([nao avalia(X) e Y],[nao X|LF]):- !,
	\+ avalia(_,X),
	verifica_condicoes([Y],LF).
verifica_condicoes([avalia(X) e Y],[N|LF]):- !,
	avalia(N,X),
	verifica_condicoes([Y],LF).

verifica_condicoes([nao avalia(X)],[nao X]):- !, \+ avalia(_,X).
verifica_condicoes([avalia(X)],[N]):- !, avalia(N,X).

verifica_condicoes([nao X e Y],[nao X|LF]):- !,
	\+ facto(_,X),
	verifica_condicoes([Y],LF).
verifica_condicoes([X e Y],[N|LF]):- !,
	facto(N,X),
	verifica_condicoes([Y],LF).

verifica_condicoes([nao X],[nao X]):- !, \+ facto(_,X).
verifica_condicoes([X],[N]):- facto(N,X).



concluir([cria_facto(F)|Y],ID,LFactos):-
	!,
	cria_facto(F,ID,LFactos),
	concluir(Y,ID,LFactos).

concluir([],_,_).



cria_facto(F,_,_):-
	facto(_,F),!.

cria_facto(F,ID,LFactos):-
	retract(ultimo_facto(N1)),
	N is N1+1,
	asserta(ultimo_facto(N)),
	assertz(justifica(N,ID,LFactos)),
	assertz(facto(N,F)),
	%get0(_),
	!.




avalia(N,P):-	P=..[Functor,Entidade,Operando,Valor],
		P1=..[Functor,Entidade,Valor1],
		facto(N,P1),
		compara(Valor1,Operando,Valor).

compara(V1,==,V):- V1==V.
compara(V1,\==,V):- V1\==V.
compara(V1,>,V):-V1>V.
compara(V1,<,V):-V1<V.
compara(V1,>=,V):-V1>=V.
compara(V1,=<,V):-V1=<V.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Visualização da base de factos

mostra_factos(StringFinal):-
	format(string(String),"~w~n",'Factos:'),
	findall(N, facto(N, _), LFactos),
	escreve_factos(LFactos,String,StringFinal).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Geração de explicações do tipo "Como"

como(N,String):-ultimo_facto(Last),Last<N,!,
	format(string(String), "~w", 'Essa conclusão não foi tirada').
como(N,String):-justifica(N,ID,LFactos),!,
	facto(N,F),
        format(string(String1), "Conclui o facto nº ~w -> ~w~n pela regra ~w~n por se ter verificado que:~n",[N,F,ID]),
	format(string(String2),"~w~n",'Factos:'),
	escreve_factos(LFactos,String2,StringFinal),
	format(string(String3),"********************************************************~n",[]),
	format(string(StringExplica),"~w~n",'Explicação:'),
	explica(LFactos,StringExplica,String4),
	format(string(String),"~w ~w ~w ~w",[String1,StringFinal,String3,String4]).

como(N,String):-facto(N,F),
	format(string(String),"O facto nº ~w -> ~w foi conhecido inicialmente~n********************************************************",[N,F]).


escreve_factos([I|R],String,StringFinal):-facto(I,F), !,
	format(string(String1),"O facto nº ~w -> ~w é verdadeiro~n",[I,F]),
	atom_concat(String,String1,String2),
	escreve_factos(R,String2,StringFinal).
escreve_factos([I|R],String,StringFinal):-
	format(string(String1),"A condição ~w é verdadeira~n",[I]),
	atom_concat(String,String1,String2),
	escreve_factos(R,String2,StringFinal).
escreve_factos([],String,StringFinal):-
	format(string(StringFinal),"~w",[String]).

explica([I|R],String,StringFinal):- \+ integer(I),!,
		explica(R,String,StringFinal).
explica([I|R],String,StringFinal):-como(I,String1),
		atom_concat(String,String1,String2),
		explica(R,String2,StringFinal).
explica([],String,StringFinal):-
     format(string(StringFinal),"~w********************************************************~n",[String]).



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Geração de explicações do tipo "Porque nao"
% Exemplo: ?- whynot(classe(meu_veículo,ligeiro)).


whynot(Facto):-
	whynot(Facto,1).

whynot(Facto,_):-
	facto(_, Facto),
	!,
	write('O facto '),write(Facto),write(' não é falso!'),nl.
whynot(Facto,Nivel):-
	encontra_regras_whynot(Facto,LLPF),
	whynot1(LLPF,Nivel).
whynot(nao Facto,Nivel):-
	formata(Nivel),write('Porque:'),write(' O facto '),write(Facto),
	write(' é verdadeiro'),nl.
whynot(Facto,Nivel):-
	formata(Nivel),write('Porque:'),write(' O facto '),write(Facto),
	write(' não está definido na base de conhecimento'),nl.

%  As explicações do whynot(Facto) devem considerar todas as regras que poderiam dar origem a conclusão relativa ao facto Facto

encontra_regras_whynot(Facto,LLPF):-
	findall((ID,LPF),
		(
		regra ID se LHS entao RHS,
		member(cria_facto(Facto),RHS),
		encontra_premissas_falsas(LHS,LPF),
		LPF \== []
		),
		LLPF).

whynot1([],_).
whynot1([(ID,LPF)|LLPF],Nivel):-
	formata(Nivel),write('Porque pela regra '),write(ID),write(':'),nl,
	Nivel1 is Nivel+1,
	explica_porque_nao(LPF,Nivel1),
	whynot1(LLPF,Nivel).

encontra_premissas_falsas([nao X e Y], LPF):-
	verifica_condicoes([nao X], _),
	!,
	encontra_premissas_falsas([Y], LPF).
encontra_premissas_falsas([X e Y], LPF):-
	verifica_condicoes([X], _),
	!,
	encontra_premissas_falsas([Y], LPF).
encontra_premissas_falsas([nao X], []):-
	verifica_condicoes([nao X], _),
	!.
encontra_premissas_falsas([X], []):-
	verifica_condicoes([X], _),
	!.
encontra_premissas_falsas([nao X e Y], [nao X|LPF]):-
	!,
	encontra_premissas_falsas([Y], LPF).
encontra_premissas_falsas([X e Y], [X|LPF]):-
	!,
	encontra_premissas_falsas([Y], LPF).
encontra_premissas_falsas([nao X], [nao X]):-!.
encontra_premissas_falsas([X], [X]).
encontra_premissas_falsas([]).

explica_porque_nao([],_).
explica_porque_nao([nao avalia(X)|LPF],Nivel):-
	!,
	formata(Nivel),write('A condição nao '),write(X),write(' é falsa'),nl,
	explica_porque_nao(LPF,Nivel).
explica_porque_nao([avalia(X)|LPF],Nivel):-
	!,
	formata(Nivel),write('A condição '),write(X),write(' é falsa'),nl,
	explica_porque_nao(LPF,Nivel).
explica_porque_nao([P|LPF],Nivel):-
	formata(Nivel),write('A premissa '),write(P),write(' é falsa'),nl,
	Nivel1 is Nivel+1,
	whynot(P,Nivel1),
	explica_porque_nao(LPF,Nivel).

formata(Nivel):-
	Esp is (Nivel-1)*5, tab(Esp).



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Metaknowledge

calcula_ultimo_facto:-
	findall(ID, ( facto(ID, _) ) , LID),
	retractall(ultimo_facto(_)),
	ultimo(Last, LID),
	assertz(ultimo_facto(Last)).

ultimo(X,[X]):-!.
ultimo(X,[_|Z]) :- last(X,Z).
