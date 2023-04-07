(function (reservationView) {
    'use strict'

    let currentSlide = 0;
    let maxCountOfSlides;

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

    reservationView.init = function () {
        getPageItems();

        maxCountOfSlides = pageItems.slides.length - 1;

        // Hide all slides except for the first one
        for (let i = 1; i < pageItems.slides.length; i++) {
            pageItems.slides[i].style.display = 'none'
        }

        // on first render, hide the button that takes the user to the next page
        pageItems.previousSlideButton.style.visibility = 'hidden'

        addPageItemEventListeners();

        populateAvailableTimes(0).then(() => {
            populateBarbers()
                .then(() => {
                    populateAvailableTimesListing(-1);
                })
        })
    }

    function getPageItems() {
        pageItems.slides = document.querySelectorAll('.slide');
        pageItems.nextSlideButton = document.querySelector('.slide-arrow-next');
        pageItems.previousSlideButton = document.querySelector(".slide-arrow-prev");
        pageItems.serviceTypeBarberButton = document.getElementById('button-service-type-barber')
        pageItems.serviceTypeHairdresserButton = document.getElementById('button-service-type-haidresser')
        pageItems.hairLengthLongButton = document.getElementById('button-hair-length-long')
        pageItems.hairLengthMediumButton = document.getElementById('button-hair-length-medium')
        pageItems.hairLengthShortButton = document.getElementById('button-hair-length-short')
        pageItems.priceText = document.getElementById('service-price-amount')
        pageItems.pageIndicatorNodes = document
            .getElementById('pageIndicatorList')
            .children;
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
    }

    function addPageItemEventListeners() {
        pageItems.nextSlideButton.addEventListener('click', onNextSlideButtonClick);
        pageItems.previousSlideButton.addEventListener('click', onPreviousSlideButtonClick)
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

        pageItems.availableTimesNavButtonNext.addEventListener('click', () => {
            document.getElementById('available-times-list-container')
                .style.opacity = 0.4;
            pageItems.availableTimesNavButtonPrevious.disabled = true;
            pageItems.availableTimesNavButtonNext.disabled = true;
            delete payload.year;
            delete payload.month;
            delete payload.day;
            delete payload.hour;
            delete payload.barberId;
            pageItems.nextSlideButton.disabled = true;
            currentAvailableTimesOffset += 4;
            populateAvailableTimes(currentAvailableTimesOffset)
                .then(() => {
                    populateAvailableTimesListing(currentBarber)
                    pageItems.availableTimesNavButtonPrevious.disabled = false;
                    pageItems.availableTimesNavButtonNext.disabled = false;
                    document.getElementById('available-times-list-container')
                        .style.opacity = 1;
                })

            if(currentAvailableTimesOffset > 0) {
                pageItems.availableTimesNavButtonPrevious
                    .style.visibility = "visible";
            }
        });

        pageItems.availableTimesNavButtonPrevious.addEventListener('click', () => {
            if (currentAvailableTimesOffset - 4 >= 0) {
                document.getElementById('available-times-list-container')
                    .style.opacity = 0.4;
                pageItems.availableTimesNavButtonPrevious.disabled = true;
                pageItems.availableTimesNavButtonNext.disabled = true;
                delete payload.year;
                delete payload.month;
                delete payload.day;
                delete payload.hour;
                delete payload.barberId;
                pageItems.nextSlideButton.disabled = true;
                currentAvailableTimesOffset -= 4;
                populateAvailableTimes(currentAvailableTimesOffset)
                    .then(() => {
                        populateAvailableTimesListing(currentBarber)
                        pageItems.availableTimesNavButtonPrevious.disabled = false;
                        pageItems.availableTimesNavButtonNext.disabled = false;
                        document.getElementById('available-times-list-container')
                            .style.opacity = 1;
                    })
            }

            if(currentAvailableTimesOffset === 0) {
                pageItems.availableTimesNavButtonPrevious
                    .style.visibility = "hidden";
            }
        });

        pageItems.confirmOrderButton.addEventListener('click', onOrderConfirm);
    }

    function onOrderConfirm(event) {
        event.preventDefault();

        fetch('/rest/sendReservation', {
            method: "POST",
            body: JSON.stringify(payload),
            credentials: 'include',
            headers: new Headers({'content-type': 'application/json'})
        }).then(response => {

            if(!response.ok) {
                throw new Error();
            }

            window.location.href = "/ajanvarausonnistui";
        })
        .catch(() => {
            window.alert("Ajan varaaminen epäonnistui");
            window.location.href = "/";
        })
    }

    reservationView.toggleService = function toggleService(button, price) {
        if (!payload.services) {
            payload.services = []
        }

        if (payload.services.includes(button.value)) {
            payload.services = payload.services
                .filter(val => val !== button.value)

            button.innerText = "+"
            button.classList.remove('reservation-toggable-service-button-selected')
            currentPrice -= price
        } else {
            payload.services.push(button.value)
            button.innerText = "-"
            button.classList.add('reservation-toggable-service-button-selected')
            currentPrice += price
        }

        pageItems.nextSlideButton.disabled = payload.services.length === 0;
        pageItems.priceText.innerText = currentPrice;
    }

    const updatePageIndicator = () => {
        for (let i = 0; i < pageItems.pageIndicatorNodes.length; i++) {
            if (i <= currentSlide) {
                pageItems.pageIndicatorNodes[i].classList.add('selected')
            } else {
                pageItems.pageIndicatorNodes[i].classList.remove('selected')
            }
        }
    }

    function onNextSlideButtonClick(event) {
        event.preventDefault();
        window.scrollTo(0, 0)
        if (currentSlide === maxCountOfSlides) {
            return;
        } else {
            currentSlide++;
        }
        pageItems.previousSlideButton.style.visibility = 'visible'

        if (currentSlide === 1 && !payload.hairLength) {
            pageItems.nextSlideButton.disabled = true
        }

        if (currentSlide === 2 && !payload.services) {
            pageItems.nextSlideButton.disabled = true
        }

        if (currentSlide === 3 && !payload.barberId) {
            pageItems.nextSlideButton.disabled = true
        }

        if(currentSlide === 4) {
            buildSummaryPage();
            pageItems.nextSlideButton.style.display = 'none'
            pageItems.confirmOrderButton.style.display = 'block'
        } else {
            pageItems.confirmOrderButton.style.display = 'none'
            pageItems.nextSlideButton.style.display = 'block'
        }

        for (let i = 0; i < pageItems.slides.length; i++) {
            if (i === currentSlide) {
                pageItems.slides[i].style.display = 'block'
            } else {
                pageItems.slides[i].style.display = 'none'
            }
        }
        updatePageIndicator()
    }


    function onPreviousSlideButtonClick(event) {
        event.preventDefault();
        window.scrollTo(0, 0)
        if (currentSlide <= 1) {
            pageItems.previousSlideButton.style.visibility = 'hidden'
        }

        if (currentSlide === 0) {
            return;
        } else {
            currentSlide--;
        }

        pageItems.nextSlideButton.disabled = false
        pageItems.nextSlideButton.style.display = 'block'
        pageItems.confirmOrderButton.style.display = 'none'

        for (let i = 0; i < pageItems.slides.length; i++) {
            if (i === currentSlide) {
                pageItems.slides[i].style.display = 'block'
            } else {
                pageItems.slides[i].style.display = 'none'
            }
        }

        updatePageIndicator()
    }

    function resetSelectedServices() {
        delete payload.services
        currentPrice = 0

        for (let i = 0; i < pageItems.serviceListButtons.length; i++) {
            pageItems.serviceListButtons[i].innerText = "+"
            pageItems.serviceListButtons[i].classList.remove('reservation-toggable-service-button-selected')
        }
        pageItems.priceText.innerText = 0
    }

    function onServiceTypeBarberButtonClick(event) {
        event.preventDefault()
        hideAllServiceTypeCheckmarks();
        pageItems.serviceTypeBarberButton.nextElementSibling.style.display = "block"
        pageItems.serviceListingsHairdressers.style.display = "none"
        pageItems.serviceListingsBarbers.style.display = "block"
        pageItems.nextSlideButton.disabled = false
        payload.serviceType = "barber"
        resetSelectedServices();
    }

    function onServiceTypeHairdresserButtonClick(event) {
        event.preventDefault()
        hideAllServiceTypeCheckmarks();
        pageItems.serviceTypeHairdresserButton.nextElementSibling.style.display = "block"
        pageItems.serviceListingsHairdressers.style.display = "block"
        pageItems.serviceListingsBarbers.style.display = "none"
        pageItems.nextSlideButton.disabled = false
        payload.serviceType = "hairdresser"
        resetSelectedServices();
    }

    function onHairLengthLongButtonClick(event) {
        event.preventDefault()
        hideAllHairLengthCheckmarks()
        pageItems.hairLengthLongButton.nextElementSibling.style.display = "block"
        payload.hairLength = "long"
        pageItems.nextSlideButton.disabled = false
    }

    function onHairLengthMediumButtonClick(event) {
        event.preventDefault()
        hideAllHairLengthCheckmarks()
        pageItems.hairLengthMediumButton.nextElementSibling.style.display = "block"
        payload.hairLength = "medium"
        pageItems.nextSlideButton.disabled = false
    }

    function onHairLengthShortButtonClick(event) {
        event.preventDefault()
        hideAllHairLengthCheckmarks()
        pageItems.hairLengthShortButton.nextElementSibling.style.display = "block"
        payload.hairLength = "short"
        pageItems.nextSlideButton.disabled = false
    }

    function onAbortButtonClick(event) {
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
        el.value = -1;
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
        typeText.innerText = payload.serviceType === "hairdresser" ? "Kampaaja" : "Parturi"
        s.appendChild(typeText);

        payload.services.forEach(service => {
            const serviceText = document.createElement('p');
            serviceText.innerText = serviceDictionary[service]
            s.appendChild(serviceText);
        })

        const hairLengthText = document.createElement('p');
        if(payload.hairLength === "short") {
            hairLengthText.innerText = "Lyhyet hiukset";
        } else if(payload.hairLength === "medium") {
            hairLengthText.innerText = "Keskipitkät hiukset";
        } else if(payload.hairLength === "short") {
            hairLengthText.innerText = "Pitkät hiukset";
        }
        s.appendChild(hairLengthText);

        const date = new Date(payload.year, payload.month - 1, payload.day);

        const dateText = document.createElement('p');
        dateText.innerText = `${getDayOfWeekText(date)} ${payload.day}.${payload.month}. klo ${payload.hour}.00`
        s.appendChild(dateText);

        if(payload.barberId === -1) {
            const barberText = document.createElement('p');
            barberText.innerText = 'Parturi-kampaaja: Kuka tahansa'
            s.appendChild(barberText);
        } else {
            barbers
                .filter(b => b.id == payload.barberId) // don't change == to === TODO
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
        pageItems.nextSlideButton.disabled = true;
        populateAvailableTimesListing(event.target.value);
        currentBarber = event.target.value;
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

        if (barberId === -1) {
            relevantAvailableTimes = availableTimes;
        } else {
            relevantAvailableTimes.today =
                availableTimes
                    .today
                    .filter(t => {
                        let barberFound = false;
                        for (let barber of t.barbersAvailable) {
                            console.log("Testing barber id " + barber.id + " against " + barberId)
                            if (barber.id == barberId) {
                                barberFound = true;
                            }
                        }
                        return barberFound;
                    })
            console.log(relevantAvailableTimes);
            relevantAvailableTimes.today =
                availableTimes
                    .today
                    .filter(t => {
                        let barberFound = false;
                        for (let barber of t.barbersAvailable) {
                            if (barber.id == barberId) {
                                barberFound = true;
                            }
                        }
                        return barberFound;
                    })
            relevantAvailableTimes.tomorrow =
                availableTimes
                    .tomorrow
                    .filter(t => {
                        let barberFound = false;
                        for (let barber of t.barbersAvailable) {
                            if (barber.id == barberId) {
                                barberFound = true;
                            }
                        }
                        return barberFound;
                    })
            relevantAvailableTimes.theDayAfter =
                availableTimes
                    .theDayAfter
                    .filter(t => {
                        let barberFound = false;
                        for (let barber of t.barbersAvailable) {
                            if (barber.id == barberId) {
                                barberFound = true;
                            }
                        }
                        return barberFound;
                    })
        }

        const to = document.getElementById('available-times-list-today');
        const tom = document.getElementById('available-times-list-tomorrow');
        const da = document.getElementById('available-times-list-day-after');

        to.innerHTML = "";
        tom.innerHTML = "";
        da.innerHTML = "";

        relevantAvailableTimes.today.forEach(a => {
            const btn = document.createElement('button');
            btn.type = "button";
            btn.classList.add('available-time-button');
            btn.innerText = `${a.hour}:00`

            btn.addEventListener('click', () => {
                if (!btn.classList.contains('available-time-button-selected')) {
                    let buttons = document.querySelectorAll('.available-time-button');
                    for (let i = 0; i < buttons.length; i++) {
                        buttons[i].classList.remove('available-time-button-selected');
                    }
                    btn.classList.add('available-time-button-selected');
                    payload.year = a.year;
                    payload.month = a.month;
                    payload.day = a.day;
                    payload.hour = a.hour;
                    payload.barberId = barberId;
                    pageItems.nextSlideButton.disabled = false
                }
            });
            to.appendChild(btn)
        })

        relevantAvailableTimes.tomorrow.forEach(a => {
            const btn = document.createElement('button');
            btn.type = "button";
            btn.classList.add('available-time-button');
            btn.innerText = `${a.hour}:00`
            btn.addEventListener('click', () => {
                if (!btn.classList.contains('available-time-button-selected')) {
                    let buttons = document.querySelectorAll('.available-time-button');
                    for (let i = 0; i < buttons.length; i++) {
                        buttons[i].classList.remove('available-time-button-selected');
                    }
                    btn.classList.add('available-time-button-selected');
                    payload.year = a.year;
                    payload.month = a.month;
                    payload.day = a.day;
                    payload.hour = a.hour;
                    payload.barberId = barberId;
                    pageItems.nextSlideButton.disabled = false
                }
            });
            tom.appendChild(btn)
        })

        relevantAvailableTimes.theDayAfter.forEach(a => {
            const btn = document.createElement('button');
            btn.type = "button";
            btn.classList.add('available-time-button');
            btn.innerText = `${a.hour}:00`
            btn.addEventListener('click', () => {
                if (!btn.classList.contains('available-time-button-selected')) {
                    let buttons = document.querySelectorAll('.available-time-button');
                    for (let i = 0; i < buttons.length; i++) {
                        buttons[i].classList.remove('available-time-button-selected');
                    }
                    btn.classList.add('available-time-button-selected');
                    payload.year = a.year;
                    payload.month = a.month;
                    payload.day = a.day;
                    payload.hour = a.hour;
                    payload.barberId = barberId;
                    pageItems.nextSlideButton.disabled = false
                }
            });
            da.appendChild(btn)
        })
    }
})(window.reservationView = window.reservationView || {})

reservationView.init();
const toggleService = reservationView.toggleService