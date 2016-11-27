function showHideButton(bt) {
    if (bt.innerHTML.search('Hide') == -1) {
        bt.innerHTML = '<span class="glyphicon glyphicon-chevron-up" aria-hidden="true"></span> Hide';
    } else {
        bt.innerHTML = '<span class="glyphicon glyphicon-chevron-down" aria-hidden="true"></span> Show More';
    }
}

function showHideRawButton(btName) {
    if (document.getElementById(btName).style.display == 'block') {
        document.getElementById(btName).style.display = 'none';
    } else {
        document.getElementById(btName).style.display = 'block';
    }
}
