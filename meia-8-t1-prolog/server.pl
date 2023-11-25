:- use_module(sp_exp2).
:- use_module(library(http/thread_httpd)).
:- use_module(library(http/http_dispatch)).
:- use_module(library(http/http_parameters)).
:- use_module(library(http/http_cors)).

% Bibliotecas JSON
:- use_module(library(http/json_convert)).
:- use_module(library(http/http_json)).


:- json_object hello_world_json(x:string).
:- json_object facto_json(facto:string).
:- json_object como_json(como:string).
:- json_object whynot_json(whynot:string).

:- http_handler(/, reply_root, []).
:- http_handler('/cria_factos', cria_factos_post, []).
:- http_handler('/arranca_motor', arranca_motor_get, []).
:- http_handler('/como', get_como, []).
%:- http_handler('/whynot', whynot_post, []).

% ===================================================================================

reply_root(_Request):-
        format('Content-type: text/plain~n~n'),
        format('HTTP Server on SWI-Prolog is ready~n').

% ===================================================================================

remove_double_quotes(String, Result) :-
    atom_string(Atom, String),  % Convert the string to an atom
    atom_chars(Atom, Chars),    % Get a list of characters
    delete(Chars, '"', NewChars), % Remove double quotes
    atom_chars(Result, NewChars). % Convert the list of characters back to an atom

write_to_file(Filename, Text) :-
    open(Filename, write, Stream),
    nl(Stream),
    write(Stream, Text),
    nl(Stream),  % Adiciona uma nova linha após o texto
    close(Stream).

cria_factos_post(Request):-
        cors_enable(Request, [
                        methods([post])]),
        http_read_json_dict(Request, DictIn),
        V = DictIn.get(velocity),
        R = DictIn.get(rpm),
        A = DictIn.get(acceleration),
        P = DictIn.get(pitch),
        N = DictIn.get(n),
        remove_double_quotes(V,F1),
        remove_double_quotes(R,F2),
        remove_double_quotes(A,F3),
        remove_double_quotes(P,F4),
        remove_double_quotes(N,N1),
        format(atom(Factos),'~w ~w ~w ~w ~w',[F1,F2,F3,F4,N1]),
        write_to_file('factos.pl',Factos),
        retractall(facto(_,_)),
        retractall(justifica(_,_,_)),
        retractall(ultimo_facto(_)),
	(consult(['knowledgeBase.pl','factos.pl']),
        reply_json(Factos, [status(201)]), !);
        reply_json(_, [status(500)]).

arranca_motor_get(Request):-
    cors_enable(Request, [methods([get])]),
    arranca_motor(String),
    Reply=facto_json(String),
    prolog_to_json(Reply, JSONObject),
    reply_json(JSONObject, [json_object(dict)]).

get_como(Request):-
        cors_enable(Request, [methods([get])]),
        http_parameters(Request,
                         [ nFacto(N, [number])]),
        como(N,String),
        Reply=como_json(String),
        prolog_to_json(Reply, JSONObject),
        reply_json(JSONObject, [json_object(dict)]).

%whynot_post(Request):-
%        cors_enable(Request,[methods([post])]),
%        http_read_json_dict(Request, DictIn),
%        X = DictIn.get(x),
%        format(atom(FactoAtom),'~w',[X]),
%        term_to_atom(Y,FactoAtom),
%        (whynot(Y,String),
%        Reply=whynot_json(String),
%        prolog_to_json(Reply, JSONObject),
%        reply_json(JSONObject, [status(201)]), !);
%        reply_json(_, [status(500)]).


% ===================================================================================

server():-
        http_server(http_dispatch, [port(4000)]).

stop():-
        retractall(facto(_,_)),
        retractall(justifica(_,_,_)),
        retractall(ultimo_facto(_)),
        http_stop_server(4000, []).

% ===================================================================================



