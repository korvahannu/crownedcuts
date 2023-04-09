(function (reservationView) {
    'use strict'

    const payload = {};

    let currentPrice = 0

    const pageItems = {}

    const availableTimes = {}

    const barbers = [];

    let currentBarber = -1;

    let currentAvailableTimesOffset = 0;

    const serviceDictionary = {
        "hairdressingCut": "Leikkaus, pesu ja kuivaus",
        "hairdressingNewStyle": "Mallinmuutosleikkaus",
        "hairdressingCutForehead": "Otsahiusten leikkaus",
        "hairdressingColor": "Hiusten värjäys",
        "hairdressingColorRootGrowth": "Juurikasvun värjäys",
        "barberBeard": "Leikkaus, pesu ja kuivaus",
        "barberColorRootGrowth": "Mallinmuutosleikkaus",
        "barberColor": "Parturi konetyö",
        "barberMachineCut": "Hiusten värjäys",
        "barberNewStyle": "Juurikasvun värjäys",
        "barberCut": "Parran muotoilu",
    }

    let slideshow;

    reservationView.init = function (slides) {
        slideshow = slides;
        slideshow.nextSlideButton.disabled = true;
        slideshow.onSlidePageChange.push(onSlidePageChange);
        getPageItems();

        addPageItemEventListeners();

        populateServices();

        populateAvailableTimes(0).then(() => {
            populateBarbers()
                .then(() => {
                    populateAvailableTimesListing(-1);
                })
        })
    }

    function getPageItems() {
        pageItems.serviceTypeBarberButton = document.getElementById('button-service-type-barber')
        pageItems.serviceTypeHairdresserButton = document.getElementById('button-service-type-haidresser')
        pageItems.hairLengthLongButton = document.getElementById('button-hair-length-long')
        pageItems.hairLengthMediumButton = document.getElementById('button-hair-length-medium')
        pageItems.hairLengthShortButton = document.getElementById('button-hair-length-short')
        pageItems.priceText = document.getElementById('service-price-amount')
        pageItems.serviceTypeCheckmarks = document.getElementsByClassName('checkmark-service-type');
        pageItems.hairLengthCheckmarks = document.getElementsByClassName('checkmark-hair-length');
        pageItems.serviceListButtons = document.getElementById('service-list')
            .getElementsByTagName('button');
        pageItems.serviceListingsHairdressers = document
            .getElementById('service-list-hairdresser')
        pageItems.serviceListingsBarbers = document
            .getElementById('service-list-barber')
        pageItems.abortButton = document.getElementById('abort-reservation');
        pageItems.shadow = document.getElementById('page-shadow');
        pageItems.messageBox = document.getElementById('messageBox');
        pageItems.cancelAbortButton = document.getElementById('cancelAbortButton');
        pageItems.confirmAbortButton = document.getElementById('confirmAbortButton');
        pageItems.barbersSelectBox = document.getElementById('form-select-barber');
        pageItems.availableTimesNavButtonNext = document.getElementById('available-times-nav-button-next');
        pageItems.availableTimesNavButtonPrevious = document.getElementById('available-times-nav-button-previous');
        pageItems.confirmOrderButton = document.getElementById('confirm-order-button');
        pageItems.availableTimesListContainer = document.getElementById('available-times-list-container');
    }

    function addPageItemEventListeners() {
        pageItems.hairLengthLongButton.addEventListener('click', onHairLengthLongButtonClick);
        pageItems.hairLengthMediumButton.addEventListener('click', onHairLengthMediumButtonClick);
        pageItems.hairLengthShortButton.addEventListener('click', onHairLengthShortButtonClick);
        pageItems.serviceTypeHairdresserButton.addEventListener('click', onServiceTypeHairdresserButtonClick);
        pageItems.serviceTypeBarberButton.addEventListener('click', onServiceTypeBarberButtonClick);
        pageItems.abortButton.addEventListener('click', onAbortButtonClick);
        pageItems.shadow.addEventListener('click', cancelAbort);
        pageItems.cancelAbortButton.addEventListener('click', cancelAbort);
        pageItems.confirmAbortButton.addEventListener('click', confirmAbort);
        pageItems.barbersSelectBox.addEventListener('change', onBarberChange);

        const changeAvailableTimeOffset = (amount) => {
            pageItems.availableTimesListContainer.style.opacity = '0.4';
            pageItems.availableTimesNavButtonPrevious.disabled = true;
            pageItems.availableTimesNavButtonNext.disabled = true;
            delete payload.year;
            delete payload.month;
            delete payload.day;
            delete payload.hour;
            delete payload.barberId;
            slideshow.nextSlideButton.disabled = true;
            currentAvailableTimesOffset += amount;
            populateAvailableTimes(currentAvailableTimesOffset)
            .then(() => {
                populateAvailableTimesListing(currentBarber)
                pageItems.availableTimesNavButtonPrevious.disabled = false;
                pageItems.availableTimesNavButtonNext.disabled = false;
                pageItems.availableTimesListContainer.style.opacity = '1';
            })

            if (currentAvailableTimesOffset > 0) {
                pageItems.availableTimesNavButtonPrevious
                    .style.visibility = "visible";
            } else {
                pageItems.availableTimesNavButtonPrevious
                    .style.visibility = "hidden";
            }
        }

        pageItems.availableTimesNavButtonNext.addEventListener('click',  () => changeAvailableTimeOffset(4));

        pageItems.availableTimesNavButtonPrevious.addEventListener('click',  () => changeAvailableTimeOffset(-4));

        pageItems.confirmOrderButton.addEventListener('click', onOrderConfirm);
    }

    function onOrderConfirm(event) {
        event.preventDefault();

        fetch('/ajanvaraus', {
            method: "POST",
            body: JSON.stringify(payload),
            credentials: 'include',
            headers: new Headers({'content-type': 'application/json'})
        }).then(response => {
            if (!response.ok) {
                throw new Error();
            }
            window.location.href = "/ajanvarausonnistui";
        })
            .catch(() => {
                window.alert("Ajan varaaminen epäonnistui");
                window.location.href = "/";
            })
    }

    function onSlidePageChange() {

        switch (slideshow.slideState.currentSlide) {
            case 1:
                slideshow.nextSlideButton.disabled = !payload.hairLength
                break;
            case 2:
                slideshow.nextSlideButton.disabled = !payload.services
                break;
            case 3:
                slideshow.nextSlideButton.disabled = !payload.barberId
                slideshow.nextSlideButton.style.display = 'block'
                pageItems.confirmOrderButton.style.display = 'none'
                break;
            case 4:
                buildSummaryPage();
                slideshow.nextSlideButton.style.display = 'none'
                pageItems.confirmOrderButton.style.display = 'block'
                break;
            default:
                pageItems.confirmOrderButton.style.display = 'none'
                slideshow.nextSlideButton.style.display = 'block'
                slideshow.nextSlideButton.disabled = false
        }
    }

    function resetSelectedServices() {
        delete payload.services
        currentPrice = 0

        for (let i = 0; i < pageItems.serviceListButtons.length; i++) {
            pageItems.serviceListButtons[i].innerText = "+"
            pageItems.serviceListButtons[i].classList.remove('reservation-toggleable-service-button-selected')
        }
        pageItems.priceText.innerText = '0';
    }

    function onServiceTypeBarberButtonClick(event) {
        event.preventDefault()
        hideAllServiceTypeCheckmarks();
        pageItems.serviceTypeBarberButton.nextElementSibling.style.display = "block"
        pageItems.serviceListingsHairdressers.style.display = "none"
        pageItems.serviceListingsBarbers.style.display = "block"
        slideshow.nextSlideButton.disabled = false
        payload.isBarberService = true
        resetSelectedServices();
    }

    function onServiceTypeHairdresserButtonClick(event) {
        event.preventDefault()
        hideAllServiceTypeCheckmarks();
        pageItems.serviceTypeHairdresserButton.nextElementSibling.style.display = "block"
        pageItems.serviceListingsHairdressers.style.display = "block"
        pageItems.serviceListingsBarbers.style.display = "none"
        slideshow.nextSlideButton.disabled = false
        payload.isBarberService = false;
        resetSelectedServices();
    }

    function onHairLengthLongButtonClick(event) {
        event.preventDefault()
        hideAllHairLengthCheckmarks()
        pageItems.hairLengthLongButton.nextElementSibling.style.display = "block"
        payload.hairLength = "long"
        slideshow.nextSlideButton.disabled = false
    }

    function onHairLengthMediumButtonClick(event) {
        event.preventDefault()
        hideAllHairLengthCheckmarks()
        pageItems.hairLengthMediumButton.nextElementSibling.style.display = "block"
        payload.hairLength = "medium"
        slideshow.nextSlideButton.disabled = false
    }

    function onHairLengthShortButtonClick(event) {
        event.preventDefault()
        hideAllHairLengthCheckmarks()
        pageItems.hairLengthShortButton.nextElementSibling.style.display = "block"
        payload.hairLength = "short"
        slideshow.nextSlideButton.disabled = false
    }

    function onAbortButtonClick(event) {
        window.scrollTo(0, 0);
        event.preventDefault();
        pageItems.shadow.style.display = 'block';
        pageItems.messageBox.style.display = 'block';
    }

    function cancelAbort(event) {
        event.preventDefault();
        event.stopPropagation();
        pageItems.shadow.style.display = 'none';
        pageItems.messageBox.style.display = 'none';
    }

    function confirmAbort(event) {
        event.preventDefault();
        event.stopPropagation();
        window.location.href = "/"
    }

    function hideAllHairLengthCheckmarks() {
        Array.from(pageItems.hairLengthCheckmarks).forEach((el) => {
            el.style.display = 'none'
        });
    }

    function hideAllServiceTypeCheckmarks() {
        Array.from(pageItems.serviceTypeCheckmarks).forEach((el) => {
            el.style.display = 'none'
        });
    }

    async function populateBarbers() {
        const data = await (await fetch('/rest/getBarbers')).json();
        data.forEach(d => barbers.push(d));

        pageItems.barbersSelectBox.innerHTML = '';

        const el = document.createElement('option');
        el.value = '-1';
        el.innerText = 'Kuka tahansa';
        pageItems.barbersSelectBox.appendChild(el);

        barbers.forEach(barber => {
            const el = document.createElement('option');
            el.value = barber.id;
            el.innerText = barber.name;
            pageItems.barbersSelectBox.appendChild(el);
        });
    }

    async function populateAvailableTimes(offset) {

        delete availableTimes.theDayAfter;
        delete availableTimes.tomorrow;
        delete availableTimes.today;

        while (!availableTimes.theDayAfter) {
            try {
                if (!availableTimes.today) {
                    const requestParams = getTimedURLSearchParams(offset)
                    const data = await (await fetch('/rest/getAvailableTimes?' + requestParams)).json();

                    if (!Array.isArray(data) || data.length < 1) {
                        offset++;
                        continue;
                    }

                    availableTimes.today = [];
                    data.forEach(a => {
                        availableTimes.today.push(a)
                    });

                    if (data.length > 0) {

                        const d = new Date(data[0].year, data[0].month - 1, data[0].day);
                        const el = document.getElementById('available-times-today');
                        el.innerText = `${getDayOfWeekText(d)} ${data[0].day}.${data[0].month}`
                    }

                    offset++;
                }

                if (!availableTimes.tomorrow) {
                    const requestParams = getTimedURLSearchParams(offset)
                    const data = await (await fetch('/rest/getAvailableTimes?' + requestParams)).json();

                    if (!Array.isArray(data) || data.length < 1) {
                        offset++;
                        continue;
                    }

                    availableTimes.tomorrow = [];
                    data.forEach(a => {
                        availableTimes.tomorrow.push(a)
                    });

                    if (data.length > 0) {

                        const d = new Date(data[0].year, data[0].month - 1, data[0].day);
                        const el = document.getElementById('available-times-tomorrow');
                        el.innerText = `${getDayOfWeekText(d)} ${data[0].day}.${data[0].month}`
                    }

                    offset++;
                }

                if (!availableTimes.theDayAfter) {
                    const requestParams = getTimedURLSearchParams(offset)
                    const data = await (await fetch('/rest/getAvailableTimes?' + requestParams)).json();

                    if (!Array.isArray(data) || data.length < 1) {
                        offset++;
                        continue;
                    }

                    availableTimes.theDayAfter = [];
                    data.forEach(a => {
                        availableTimes.theDayAfter.push(a)
                    });

                    if (data.length > 0) {

                        const d = new Date(data[0].year, data[0].month - 1, data[0].day);

                        const el = document.getElementById('available-times-day-after');
                        el.innerText = `${getDayOfWeekText(d)} ${data[0].day}.${data[0].month}`
                    }
                }
            } catch (error) {
                offset++;
            }
        }
    }

    function getTimedURLSearchParams(offset) {
        const date = new Date();

        Date.prototype.addDays = function (days) {
            let date = new Date(this.valueOf());
            date.setDate(date.getDate() + days);
            return date;
        }

        return new URLSearchParams({
            year: date.getFullYear(),
            month: date.getMonth() + 1,
            day: date.addDays(offset).getDate()
        });
    }

    function buildSummaryPage() {
        const s = document.getElementById('summary-details-container');
        s.innerHTML = "";

        const typeText = document.createElement('p');
        typeText.innerText = payload.isBarberService === false ? "Kampaaja" : "Parturi"
        s.appendChild(typeText);

        payload.services.forEach(service => {
            const serviceText = document.createElement('p');
            serviceText.innerText = serviceDictionary[service]
            s.appendChild(serviceText);
        })

        const hairLengthText = document.createElement('p');
        if (payload.hairLength === "short") {
            hairLengthText.innerText = "Lyhyet hiukset";
        } else if (payload.hairLength === "medium") {
            hairLengthText.innerText = "Keskipitkät hiukset";
        } else if (payload.hairLength === "short") {
            hairLengthText.innerText = "Pitkät hiukset";
        }
        s.appendChild(hairLengthText);

        const date = new Date(payload.year, payload.month - 1, payload.day);

        const dateText = document.createElement('p');
        dateText.innerText = `${getDayOfWeekText(date)} ${payload.day}.${payload.month}. klo ${payload.hour}.00`
        s.appendChild(dateText);

        if (payload.barberId === -1) {
            const barberText = document.createElement('p');
            barberText.innerText = 'Parturi-kampaaja: Kuka tahansa'
            s.appendChild(barberText);
        } else {
            barbers
                .filter(b => b.id === payload.barberId) // don't change == to === TODO
                .forEach(b => {
                    const barberText = document.createElement('p');
                    barberText.innerText = `Parturi-kampaaja: ${b.name}`
                    s.appendChild(barberText);
                });
        }

        const priceText = document.createElement('p');
        priceText.innerText = `Hinta: ${currentPrice}.00€`;
        s.appendChild(priceText);
    }

    function onBarberChange(event) {
        delete payload.year;
        delete payload.month;
        delete payload.day;
        delete payload.hour;
        delete payload.barberId;
        slideshow.nextSlideButton.disabled = true;
        currentBarber = parseInt(event.target.value);
        populateAvailableTimesListing(currentBarber);
    }

    function getDayOfWeekText(date) {
        const n = date.getDay();

        switch (n) {
            case 0:
                return "Sunnuntai";
            case 1:
                return "Maanantai";
            case 2:
                return "Tiistai";
            case 3:
                return "Keskiviikko";
            case 4:
                return "Torstai";
            case 5:
                return "Perjantai";
            default:
                return "Lauantai";
        }
    }

    function populateAvailableTimesListing(barberId) {
        let relevantAvailableTimes = {};

        const barberFilter = (time) => {
            let barberFound = false;

            if(!time.hasOwnProperty('barbersAvailable')) {
                return barberFound;
            }

            for (let barber of time.barbersAvailable) {
                if (barber.id === barberId) {
                    barberFound = true;
                }
            }
            return barberFound;
        }

        if (barberId === -1) {
            relevantAvailableTimes = availableTimes;
        } else {
            relevantAvailableTimes.today =
                availableTimes
                    .today
                    .filter(barberFilter);
            relevantAvailableTimes.tomorrow =
                availableTimes
                    .tomorrow
                    .filter(barberFilter);
            relevantAvailableTimes.theDayAfter =
                availableTimes
                    .theDayAfter
                    .filter(barberFilter);
        }

        const to = document.getElementById('available-times-list-today');
        const tom = document.getElementById('available-times-list-tomorrow');
        const da = document.getElementById('available-times-list-day-after');

        to.innerHTML = "";
        tom.innerHTML = "";
        da.innerHTML = "";

        const createNoTimesParagraph = () => {
            const non = document.createElement('p');
            non.innerText = "Kyseisellä parturi-kampaajalla ei ole vapaita aikoja tälle päivälle."
            non.classList.add('no-available-times-text');
            return non;
        }

        const createTimeButton = (year, month, day, hour, barberId) => {
            const btn = document.createElement('button');
            btn.type = "button";
            btn.classList.add('available-time-button');
            btn.innerText = `${hour}:00`

            const clearSelectedTime = () => {
                let buttons = document.querySelectorAll('.available-time-button');
                for (let i = 0; i < buttons.length; i++) {
                    buttons[i].classList.remove('available-time-button-selected');
                }
            }

            btn.addEventListener('click', () => {
                if (!btn.classList.contains('available-time-button-selected')) {
                    clearSelectedTime();
                    btn.classList.add('available-time-button-selected');
                    payload.year = year;
                    payload.month = month;
                    payload.day = day;
                    payload.hour = hour;
                    payload.barberId = barberId;
                    slideshow.nextSlideButton.disabled = false
                }
            });

            return btn;
        }

        if (!relevantAvailableTimes.today || relevantAvailableTimes.today.length < 1) {
            to.appendChild(createNoTimesParagraph());
        }

        relevantAvailableTimes.today.forEach(a => {
            to.appendChild(createTimeButton(a.year, a.month, a.day, a.hour, barberId));
        })

        if (!relevantAvailableTimes.tomorrow || relevantAvailableTimes.tomorrow.length < 1) {
            tom.appendChild(createNoTimesParagraph());
        }

        relevantAvailableTimes.tomorrow.forEach(a => {
            tom.appendChild(createTimeButton(a.year, a.month, a.day, a.hour, barberId));
        })

        if (!relevantAvailableTimes.theDayAfter || relevantAvailableTimes.theDayAfter.length < 1) {
            da.appendChild(createNoTimesParagraph());
        }

        relevantAvailableTimes.theDayAfter.forEach(a => {
            da.appendChild(createTimeButton(a.year, a.month, a.day, a.hour, barberId));
        })
    }

    function toggleService(value, button, price) {
        if (!payload.services) {
            payload.services = []
        }

        if (payload.services.includes(value)) {
            payload.services = payload.services
                .filter(val => val !== value)

            button.innerText = "+"
            button.classList.remove('reservation-toggleable-service-button-selected')
            currentPrice -= price
        } else {
            payload.services.push(value)
            button.innerText = "-"
            button.classList.add('reservation-toggleable-service-button-selected')
            currentPrice += price
        }

        slideshow.nextSlideButton.disabled = payload.services.length === 0;
        pageItems.priceText.innerText = currentPrice;
    }

    function createServiceListing(value, name, price) {
        const div = document.createElement('div');
        div.classList.add('service-listing');

        const span1 = document.createElement('span');
        span1.classList.add('service-title');
        span1.innerText = name;

        const span2 = document.createElement('span');
        span2.classList.add('price');
        span2.innerText = `${price}.00€`;

        const button = document.createElement('button');
        button.classList.add('reservation-toggleable-service-button');
        button.type = 'button';

        button.addEventListener('click', event => {
           event.preventDefault();
           // TODO, change so that you dont have to pass button here
           toggleService(value, button, 45);
        });

        div.appendChild(span1);
        div.appendChild(span2);
        div.appendChild(button);
        return div;
    }

    async function populateServices() {

        const result = await (await fetch('/rest/getAllServices')).json();
        pageItems.serviceListingsBarbers.innerHTML = '';
        pageItems.serviceListingsHairdressers.innerHTML = '';

        result.forEach(service => {
           if(service.isBarberService === true) {
                pageItems.serviceListingsBarbers.appendChild(
                    createServiceListing(service.id, service.name, service.price)
                );
           } else {
               pageItems.serviceListingsHairdressers.appendChild(
                   createServiceListing(service.id, service.name, service.price)
               );
           }
        });

    }
})(window.reservationView = window.reservationView || {})