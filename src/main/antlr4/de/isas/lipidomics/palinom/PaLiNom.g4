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
: hg_mgl hg_sep fa
;

/* Diacylglycerols */
dgl
  : hg_dgl hg_sep fa unsorted_fa_sep fa 
  | hg_dgl hg_sep fa sorted_fa_sep fa
  ;

/* */
sgl
  : hg_sgl hg_sep fa unsorted_fa_sep fa 
  | hg_sgl hg_sep fa sorted_fa_sep fa
  ;

/* Triacylglycerols */
tgl
  : hg_tgl hg_sep fa unsorted_fa_sep fa unsorted_fa_sep fa 
  | hg_tgl hg_sep fa sorted_fa_sep fa sorted_fa_sep fa
  ;

/* Glycerolipid headgroups */
hg_mgl
  : MAG
  ;

MAG: 'MAG';

hg_dgl
  : DAG
  ;
DAG
  : 'DAG'
  ;

hg_sgl
  : MGDG | DGDG | SQDG
  ;

MGDG: 'MGDG';
DGDG: 'DGDG';
SQDG: 'SQDG';

hg_tgl
  : HG_TGL
  ;
HG_TGL: 'TAG';

/* Phospholipids */

pl: lpl | dpl | pl_o | cl | mlcl;
pl_o: lpl_o | dpl_o;
lpl: hg_lpl hg_sep fa;
lpl_o: hg_lpl_o hg_sep fa;
dpl: hg_pl hg_sep fa unsorted_fa_sep fa | hg_pl hg_sep fa sorted_fa_sep fa;
dpl_o: hg_pl_o hg_sep fa unsorted_fa_sep fa | hg_pl_o hg_sep fa sorted_fa_sep fa;
cl: hg_cl hg_sep fa unsorted_fa_sep fa unsorted_fa_sep fa unsorted_fa_sep fa | hg_cl hg_sep fa sorted_fa_sep fa sorted_fa_sep fa sorted_fa_sep fa;
mlcl: hg_mlcl hg_sep fa unsorted_fa_sep fa unsorted_fa_sep fa | hg_mlcl hg_sep fa sorted_fa_sep fa sorted_fa_sep fa;

hg_cl: CL;
CL: 'CL';
hg_mlcl: MLCL;
MLCL: 'MLCL';
hg_pl: BMP | CDPDAG | DMPE | MMPE | PA | PC | PE | PET | PG | PI | PIP | PIP2 | PIP3 | PS;
BMP: 'BMP';
CDPDAG: 'CDPDAG';
DMPE: 'DMPE';
MMPE: 'MMPE';
PA: 'PA';
PC: 'PC';
PE: 'PE';
PET: 'PEt';
PG: 'PG';
PI: 'PI';
PIP: 'PIP';
PIP2: 'PIP2';
PIP3: 'PIP3';
PS: 'PS';

hg_lpl: LPA | LPC | LPE | LPG | LPI | LPS;
LPA: 'LPA';
LPC: 'LPC';
LPE: 'LPE';
LPG: 'LPG';
LPI: 'LPI';
LPS: 'LPS';

hg_lpl_o: LPC_O | LPE_O;
LPC_O: 'LPC O';
LPE_O: 'LPE O';
hg_pl_o: PC_O | PE_O;
PC_O: 'PC O';
PE_O: 'PE O';

/* Sphingolipids */
sl: lsl | dsl;
lsl: hg_lsl hg_sep lcb;
dsl: hg_dsl hg_sep lcb sorted_fa_sep fa;

hg_lsl: LCB | LCBP | LHEXCER | LSM;
LCB: 'LCB';
LCBP: 'LCBP';
LHEXCER: 'LHexCer';
LSM: 'LSM';

hg_dsl: CER | CERP | EPC | GB3 | GB4 | GD3 | GM3 | GM4 | HEX2CER | HEXCER | IPC | MIP2C | MIPC | SHEXCER | SM;
CER: 'Cer';
CERP: 'CerP';
EPC: 'EPC';
GB3: 'GB3';
GB4: 'GB4';
GD3: 'GD3';
GM3: 'GM3';
GM4: 'GM4';
HEX2CER: 'Hex2Cer';
HEXCER: 'HexCer';
IPC: 'IPC';
MIP2C: 'M(IP)2C';
MIPC: 'MIPC';
SHEXCER: 'SHexCer';
SM: 'SM';

/* Cholesterol lipids */
cholesterol: ch | che;
ch: CH;
CH: 'Ch';
che: hg_che hg_sep fa;
hg_che: CHE;
CHE: 'ChE';

/* Mediators */

mediator:  
M_10_HDOHE |
M_11_HDOHE |
M_11_HETE |
M_11_12_DHET |
M_11_12_EET |
M_12_HEPE  |
M_12_HETE |
M_12_HHTRE |
M_12_OXOETE |
M_12_13_EPOME |
M_13_HODE |
M_13_HOTRE |
M_14_15_DHET |
M_14_15_EET |
M_14_15_EPETE |
M_15_HEPE |
M_15_HETE |
M_15_D_PGJ2 |
M_16_HDOHE |
M_16_HETE |
M_18_HEPE |
M_5_HEPE |
M_5_HETE |
M_5_HPETE |
M_5_OXOETE |
M_5_12_DIHETE |
M_5_6_DIHETE |
M_5_6_15_LXA4 |
M_5_6_EET |
M_8_HDOHE |
M_8_HETE |
M_8_9_DHET |
M_8_9_EET |
M_9_HEPE |
M_9_HETE |
M_9_HODE |
M_9_HOTRE |
M_9_10_EPOME |
M_AA |
M_ALPHA_LA |
M_DHA |
M_EPA |
M_LINOLEIC_ACID |
M_LTB4 |
M_LTC4 |
M_LTD4 |
M_MARESIN_1 |
M_PALMITIC_ACID |
M_PGB2 |
M_PGD2 |
M_PGE2 |
M_PGF2ALPHA |
M_PGI2 |
M_RESOLVIN_D1 |
M_RESOLVIN_D2 |
M_RESOLVIN_D3 |
M_RESOLVIN_D5 |
M_TETRANOR_12_HETE |
M_TXB1 |
M_TXB2 |
M_TXB3 ;

M_10_HDOHE: '10-HDoHE';
M_11_HDOHE: '11-HDoHE';
M_11_HETE: '11-HETE';
M_11_12_DHET: '11,12-DHET';
M_11_12_EET: '11(12)-EET';
M_12_HEPE : '12-HEPE';
M_12_HETE: '12-HETE';
M_12_HHTRE: '12-HHTrE';
M_12_OXOETE: '12-OxoETE';
M_12_13_EPOME: '12(13)-EpOME';
M_13_HODE: '13-HODE';
M_13_HOTRE: '13-HOTrE';
M_14_15_DHET: '14,15-DHET';
M_14_15_EET: '14(15)-EET';
M_14_15_EPETE: '14(15)-EpETE';
M_15_HEPE: '15-HEPE';
M_15_HETE: '15-HETE';
M_15_D_PGJ2: '15d-PGJ2';
M_16_HDOHE: '16-HDoHE';
M_16_HETE: '16-HETE';
M_18_HEPE: '18-HEPE';
M_5_HEPE: '5-HEPE';
M_5_HETE: '5-HETE';
M_5_HPETE: '5-HpETE';
M_5_OXOETE: '5-OxoETE';
M_5_12_DIHETE: '5,12-DiHETE';
M_5_6_DIHETE: '5,6-DiHETE';
M_5_6_15_LXA4: '5,6,15-LXA4';
M_5_6_EET: '5(6)-EET';
M_8_HDOHE: '8-HDoHE';
M_8_HETE: '8-HETE';
M_8_9_DHET: '8,9-DHET';
M_8_9_EET: '8(9)-EET';
M_9_HEPE: '9-HEPE';
M_9_HETE: '9-HETE';
M_9_HODE: '9_HODE';
M_9_HOTRE: '9-HOTrE';
M_9_10_EPOME: '9(10)-EpOME';
M_AA: 'AA';
M_ALPHA_LA: 'alpha-LA';
M_DHA: 'DHA';
M_EPA: 'EPA';
M_LINOLEIC_ACID: 'Linoleic acid';
M_LTB4: 'LTB4';
M_LTC4: 'LTC4';
M_LTD4: 'LTD4';
M_MARESIN_1: 'Maresin 1';
M_PALMITIC_ACID: 'Palmitic acid';
M_PGB2: 'PGB2';
M_PGD2: 'PGD2';
M_PGE2: 'PGE2';
M_PGF2ALPHA: 'PGF2alpha';
M_PGI2: 'PGI2';
M_RESOLVIN_D1: 'Resolvin D1';
M_RESOLVIN_D2: 'Resolvin D2';
M_RESOLVIN_D3: 'Resolvin D3';
M_RESOLVIN_D5: 'Resolvin D5';
M_TETRANOR_12_HETE: 'tetranor-12-HETE';
M_TXB1: 'TXB1';
M_TXB2: 'TXB2';
M_TXB3: 'TXB3';

fa: fa_pure | fa_pure ether;
fa_pure
  : carbon c_db_sep db
  | carbon c_db_sep db db_hydroxyl_sep hydro
  ;
ether: 'a' | 'p';

/* Long chain base */
lcb: carbon c_db_sep db db_hydroxyl_sep hydro;


carbon
  : DIGIT
  ;

db: db_count | db_count LRB db_position RRB;

db_count
  : DIGIT
  ;

db_position: DIGIT cistrans | db_position db_pos_sep db_position;

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

hg_sep
  : SPACE
  ;

COLON
  : ':'
  ;

c_db_sep: COLON;

SEMICOLON
  : ';'
  ;

db_hydroxyl_sep: SEMICOLON;

UNDERSCORE
  : '_'
  ;

unsorted_fa_sep
  : UNDERSCORE
  ;

SLASH
  : '/'
  ;

sorted_fa_sep
  : SLASH
  ;

COMMA: ',';

db_pos_sep: COMMA;