'use strict';

function requiredValidator(elementId) {
    const el = document.getElementById(elementId);
    const val = el.value.toString();
    if(val && val !== "") {
        return true;
    }
    el.classList.add('error');
    next(el).style.display = 'block';
    return false;
}

function phonenumberValidator(elementId) {
    const el = document.getElementById(elementId);
    const val = el.value.toString();
    if(val === "" || val !== "") { // this is always true, this method exists as a placeholder
        return true;
    }
    el.classList.add('error');
    next(el).style.display = 'block';
    return false;
}

function dateOfBirthValidator(elementId) {
    const el = document.getElementById(elementId);
    const val = el.value.toString();
    const regex = /^(0?[1-9]|[12][0-9]|3[01])[\/\-.](0?[1-9]|1[012])[\/\-.]\d{4}$/;
    if(val === "" || regex.test(val)) {
        return true;
    }
    el.classList.add('error');
    next(el).style.display = 'block';
    return false;
}

function next(elem) {
    do {
        elem = elem.nextSibling;
    } while (elem && elem.nodeType !== 1);
    return elem;
}
