grammar PaLiNom;

lipidIdentifier
  : category +
  ;

category
  : pl
  ;

pl
  : hg SPACE fa UNDERSCORE fa
  | hg SPACE fa SLASH fa
  ;

hg
  : BMP
  | PA
  | PC
  | PE
  ;

BMP
  : 'BMP'
  ;

PA
  : 'PA'
  ;
PC
  : 'PC'
  ;
PE
  : 'PE'
  ;

fa
  : carbon COLON db
  | carbon COLON db SEMICOLON hydro
  ;

carbon
  : DIGIT
  ;

db
  : DIGIT
  ;

hydro
  : DIGIT
  ;

DIGIT
  : [0-9]+
  ;

LDIGIT
  : [1-9]+
  ;

SPACE
  : ' '
  ;

COLON
  : ':'
  ;
SEMICOLON
  : ';'
  ;

UNDERSCORE
  : '_'
  ;

SLASH
  : '/'
  ;