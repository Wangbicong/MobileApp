function setContent(x) {
    x = x.replace(/\*/g, '×')
         .replace(/\//g, '÷')
         .replace(/([\+\-×÷])/g, '<span class="op"> $1 </span>')
         .replace(/([\(\)])/g, '<span class="pr">$1</span>')


    if (!x.length) {
        x = '<span> </span>'
    }

    document.getElementById("screen").innerHTML = x
}

