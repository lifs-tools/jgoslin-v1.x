/*
 * ANTLR4 grammar for lipid subspecies identifiers followed by J.K. Pauling et al. 2017, PLoS One, 12(11):e0188394.
 */
grammar PaLiNom;

lipidIdentifier
  : category
  | category adduct_term
  ;

/* Support for adducts */
ADD_SUM_FORMULA: 'M';

adduct_term
  : LSQB ADD_SUM_FORMULA adduct RSQB charge charge_sign
  ;
adduct: '+H' | '+2H' | '+NH4' | '-H' | '-2H' | '+HCOO' | '+CH3COO' | '+K' | '+Na';

charge: LDIGIT;

charge_sign: '-' | '+';

category
  : gl | pl | sl | cholesterol | mediator
  ;

/*
 * Glycerolipids
 */
gl
  : mgl | dgl | sgl | tgl
  ;

/* Monoacylglycerols */
mgl
: hg_mgl HG_SEP fa
;

/* Diacylglycerols */
dgl
  : hg_dgl HG_SEP fa UNSORTED_FA_SEP fa 
  | hg_dgl HG_SEP fa SORTED_FA_SEP fa
  ;

/* */
sgl
  : hg_sgl HG_SEP fa UNSORTED_FA_SEP fa 
  | hg_sgl HG_SEP fa SORTED_FA_SEP fa
  ;

/* Triacylglycerols */
tgl
  : hg_tgl HG_SEP fa UNSORTED_FA_SEP fa UNSORTED_FA_SEP fa 
  | hg_tgl HG_SEP fa SORTED_FA_SEP fa SORTED_FA_SEP fa
  ;

/* Glycerolipid headgroups */
hg_mgl
  : 'MAG'
  ;

hg_dgl
  : 'DAG'
  ;

hg_sgl
  : 'MGDG' | 'DGDG' | 'SQDG'
  ;

hg_tgl
  : 'TAG'
  ;

/* Phospholipids */

pl: lpl | dpl | pl_o | cl | mlcl;
pl_o: lpl_o | dpl_o;
lpl: hg_lpl HG_SEP fa;
lpl_o: hg_lpl_o HG_SEP fa;
dpl: hg_pl HG_SEP fa UNSORTED_FA_SEP fa | hg_pl HG_SEP fa SORTED_FA_SEP fa;
dpl_o: hg_pl_o HG_SEP fa UNSORTED_FA_SEP fa | hg_pl_o HG_SEP fa SORTED_FA_SEP fa;
cl: hg_cl HG_SEP fa UNSORTED_FA_SEP fa UNSORTED_FA_SEP fa UNSORTED_FA_SEP fa | hg_cl HG_SEP fa SORTED_FA_SEP fa SORTED_FA_SEP fa SORTED_FA_SEP fa;
mlcl: hg_mlcl HG_SEP fa UNSORTED_FA_SEP fa UNSORTED_FA_SEP fa | hg_mlcl HG_SEP fa SORTED_FA_SEP fa SORTED_FA_SEP fa;

hg_cl: 'CL';
hg_mlcl: 'MLCL';
hg_pl: 'BMP' | 'CDPDAG' | 'DMPE' | 'MMPE' | 'PA' | 'PC' | 'PE' | 'PEt' | 'PG' | 'PI' | 'PIP' | 'PIP2' | 'PIP3' | 'PS';
hg_lpl: 'LPA' | 'LPC' | 'LPE' | 'LPG' | 'LPI' | 'LPS';
hg_lpl_o: 'LPC O' | 'LPE O';
hg_pl_o: 'PC O' | 'PE O';

/* Sphingolipids */
sl: lsl | dsl;
lsl: hg_lsl HG_SEP lcb;
dsl: hg_dsl HG_SEP lcb SORTED_FA_SEP fa;

hg_lsl: 'LCB' | 'LCBP' | 'LHexCer' | 'LSM';
hg_dsl: 'Cer' | 'CerP' | 'EPC' | 'GB3' | 'GB4' | 'GD3' | 'GM3' | 'GM4' | 'Hex2Cer' | 'HexCer' | 'IPC' | 'M(IP)2C' | 'MIPC' | 'SHexCer' | 'SM';

/* Cholesterol lipids */
cholesterol: ch | che;
ch: 'Ch';
che: hg_che HG_SEP fa;
hg_che: 'ChE';

/* Mediators */

mediator: '10-HDoHE' | '11-HDoHE' | '11-HETE' | '11,12-DHET' | '11(12)-EET'| '12-HEPE' | '12-HETE' | '12-HHTrE' | '12-OxoETE' | '12(13)-EpOME' | '13-HODE' | '13-HOTrE' | '14,15-DHET' | '14(15)-EET' | '14(15)-EpETE' | '15-HEPE' | '15-HETE' | '15d-PGJ2' | '16-HDoHE' | '16-HETE' | '18-HEPE' | '5-HEPE' | '5-HETE' | '5-HpETE' | '5-OxoETE' | '5,12-DiHETE' | '5,6-DiHETE' | '5,6,15-LXA4' | '5(6)-EET' | '8-HDoHE' | '8-HETE' | '8,9-DHET' | '8(9)-EET' | '9-HEPE' | '9-HETE' | '9-HODE' | '9-HOTrE' | '9(10)-EpOME' | 'AA' | 'alpha-LA' | 'DHA' | 'EPA' | 'Linoleic acid' | 'LTB4' | 'LTC4' | 'LTD4' | 'Maresin 1' | 'Palmitic acid' | 'PGB2' | 'PGD2' | 'PGE2' | 'PGF2alpha' | 'PGI2' | 'Resolvin D1' | 'Resolvin D2' | 'Resolvin D3' | 'Resolvin D5' | 'tetranor-12-HETE' | 'TXB1' | 'TXB2' | 'TXB3';

fa: fa_pure | fa_pure ether;
fa_pure
  : carbon C_DB_SEP db
  | carbon C_DB_SEP db DB_HYDROXYL_SEP hydro
  ;
ether: 'a' | 'p';

/* Long chain base */
lcb: carbon C_DB_SEP db DB_HYDROXYL_SEP hydro;


carbon
  : DIGIT
  ;

db: db_count | db_count LRB db_position RRB;

db_count
  : DIGIT
  ;

db_position: DIGIT cistrans | db_position DB_POS_SEP db_position;

cistrans: 'E' | 'Z';

hydro
  : DIGIT
  ;

/* Left round bracket ( */
LRB: '(';
/* Right round bracket ) */
RRB: ')';
/* Left square bracket [ */
LSQB
  : '['
  ;
/* Right square bracket ] */
RSQB
  : ']'
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

HG_SEP
  : SPACE
  ;

COLON
  : ':'
  ;

C_DB_SEP: COLON;

SEMICOLON
  : ';'
  ;

DB_HYDROXYL_SEP: SEMICOLON;

UNDERSCORE
  : '_'
  ;

UNSORTED_FA_SEP
  : UNDERSCORE
  ;

SLASH
  : '/'
  ;

SORTED_FA_SEP
  : SLASH
  ;

COMMA: ',';

DB_POS_SEP: COMMA;