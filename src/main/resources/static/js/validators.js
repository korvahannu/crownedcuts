'use strict';

function requiredValidator(elementId) {
    const {el, val} = getElementAndValue(elementId);
    if (val && val.trim() !== "") {
        return true;
    }
    return addErrorAndReturnFalse(el);
}

function phonenumberValidator(elementId) {
    const {el, val} = getElementAndValue(elementId);
    if (val === "" || val !== "") { // this is always true, this method exists as a placeholder
        return true;
    }
    return addErrorAndReturnFalse(el);
}

function dateOfBirthValidator(elementId) {
    const {el, val} = getElementAndValue(elementId);
    const regex = /^(0?[1-9]|[12][0-9]|3[01])[\/\-.](0?[1-9]|1[012])[\/\-.]\d{4}$/;
    if (val === "" || regex.test(val)) {
        return true;
    }
    return addErrorAndReturnFalse(el);
}

function matcherValidator(firstElementId, secondElementId) {
    const firstElementInfo = getElementAndValue(firstElementId);
    const secondElementInfo = getElementAndValue(secondElementId);

    if (firstElementInfo.val === secondElementInfo.val) {
        return true;
    }

    firstElementInfo.el.classList.add('error');
    next(firstElementInfo.el).style.display = 'block';
    secondElementInfo.el.classList.add('error');
    next(secondElementInfo.el).style.display = 'block';
    return false;
}

function passwordValidator(elementId) {
    const { el, val } = getElementAndValue(elementId);

    if(val.length >= 5) {
        return true;
    }

    return addErrorAndReturnFalse(el);
}

function getElementAndValue(elementId) {
    const el = document.getElementById(elementId);
    const val = el.value.toString();
    return {el, val};
}

function addErrorAndReturnFalse(el) {
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
