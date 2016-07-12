grammar Message;

oneLine:
    show |
    msg |
    EOF;

show: '(' 'show' stepNumber=Float
      '(' 'pm' playMode=Float ')'
      '(' 'tm' leftTeam=String rightTeam=String leftScore=Float
      rightScore=Float ')'
      ball
      (player)+
      ')';

ball: '(' '(' 'b' ')' x=Float y=Float Float Float ')';

player: '(' '(' side=String number=Float ')' Float flags=Hexa x=Float y=Float
(Float)+ playerView? playerS? playerFocus? playerCounter? ')';

playerView: '(' 'v' String Float ')';

playerS: '(' 's' Float Float Float Float ')';

playerFocus: '(' 'f' String Float ')';

playerCounter: '(' 'c' (Float)* ')';

msg: '(' 'msg' stepNumber=Float Float '"' '(' (info)* ')' '"' ')' ;

info: infoText=(String | Float);

// skip lines with parameters
PARAM: ('(server_param' | '(player_param' | '(player_type') ~[]* -> skip;
// skip all white spaces
WS: [ \t]+ -> skip;
String: ('A'..'Z' | 'a'..'z' | '_')+;
Float: '-'?'0'..'9'+ ('.''0'..'9'+)? ;
Hexa: '0x'('0'..'9' | 'a'..'f' | 'A'..'F')+;

ErrorCharacter : . ;