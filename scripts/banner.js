const chalkRainbow = require('chalk-rainbow');

const banner =
`             ┏━━━━━━━━━┅┅~~ ~
             ┃ $msg
             ┗━━━━━━━━━┅┅~~ ~
 /\\**/\\       │
( o_o  )_     │
 (u--u   \\_)  │                                         
  (||___   )==\\                                         
,dP"/b/=( /P"/b\\                                        
|8 || 8\\=== || 8                                        
\`b,  ,P  \`b,  ,P                                        
  """\`     """\`                                         
`.split("\n");

banner.forEach(row => {
  console.log('    '+row.replace('$msg',chalkRainbow('id-front is starting')))
})


//source -> http://www.asciiartfarts.com/
