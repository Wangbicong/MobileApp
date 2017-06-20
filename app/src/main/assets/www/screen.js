// x::string - the expression
// y::float - the result (OPTIONAL)
function setContent(x, y) {
    document.getElementById('screen').innerHTML = render(x, y)
}

function calculate(x) {
    try {
        return ''+eval(x)
    } catch(e) {
        return 'error'
    }
}

function render(x, y) {
    if (x == 'error')
        return '<span class="er"> Error </span>'

    x = x.replace(/\*/g, '&times;')
         .replace(/\//g, '&divide;')
         .replace(/([\+\-×÷])/g, '<span class="op"> $1 </span>')
         .replace(/([\(\)])/g, '<span class="pr">$1</span>')


    if (!x.length) {
        x = '<span> </span>'
    }

    if (y != null) {
        x += '<span class="rs"> = ' + y + '</span>'
    }

    return x
}