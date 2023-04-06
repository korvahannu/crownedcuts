(function (reservationView) {
    'use strict'

    let currentSlide = 0;
    let maxCountOfSlides;

    const payload = {};
    let currentPrice = 0

    const pageItems = {}

    reservationView.init = function () {
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

        maxCountOfSlides = pageItems.slides.length - 1;

        // Hide all slides except for the first one
        for (let i = 1; i < pageItems.slides.length; i++) {
            pageItems.slides[i].style.display = 'none'
        }

        // on first render, hide the button that takes the user to the next page
        pageItems.previousSlideButton.style.visibility = 'hidden'

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
})(window.reservationView = window.reservationView || {})

reservationView.init();
const toggleService = reservationView.toggleService