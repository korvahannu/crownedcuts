(function (reservationView) {
    'use strict'
    // Select all slides
    const slides = document.querySelectorAll(".slide");

    // loop through slides and set each slides translateX
    for (let i = 1; i < slides.length; i++) {
        slides[i].style.display = 'none'
    }
    // select next slide button
    const nextSlide = document.querySelector(".slide-arrow-next");

    // current slide counter
    let curSlide = 0;
    // maximum number of slides
    let maxSlide = slides.length - 1;

    const payload = {};
    let currentPrice = 0

    reservationView.start = function () {
        console.log('Jello, world')
    }

    const updatePageIndicator = () => {
        const items =
            document
                .getElementById('pageIndicatorList')
                .children;

        for (let i = 0; i < items.length; i++) {
            if (i <= curSlide) {
                items[i].classList.add('selected')
            } else {
                items[i].classList.remove('selected')
            }
        }
    }

    // add event listener and navigation functionality
    nextSlide.addEventListener("click", function (event) {
        event.preventDefault();
        // check if current slide is the last and reset current slide
        if (curSlide === maxSlide) {
            return;
        } else {
            curSlide++;
        }
        prevSlide.style.display = "block"

        if (curSlide === 1 && !payload.hairLength) {
            nextSlide.disabled = true
        }

        if (curSlide === 2 && !payload.services) {
            nextSlide.disabled = true
        }

        for (let i = 0; i < slides.length; i++) {
            if (i === curSlide) {
                slides[i].style.display = 'block'
            } else {
                slides[i].style.display = 'none'
            }
        }
        updatePageIndicator()
    });

    // select next slide button
    const prevSlide = document.querySelector(".slide-arrow-prev");
    prevSlide.style.display = "none"

    // add event listener and navigation functionality
    prevSlide.addEventListener("click", function (event) {
        event.preventDefault();
        if (curSlide <= 1) {
            prevSlide.style.display = "none"
        }

        // check if current slide is the first and reset current slide to last
        if (curSlide === 0) {
            return;
        } else {
            curSlide--;
        }

        nextSlide.disabled = false

        for (let i = 0; i < slides.length; i++) {
            if (i === curSlide) {
                slides[i].style.display = 'block'
            } else {
                slides[i].style.display = 'none'
            }
        }

        updatePageIndicator()
    });

    const serviceTypeBarberButton = document.getElementById('button-service-type-barber')
    const serviceTypeHaidresserButton = document.getElementById('button-service-type-haidresser')
    const hairLengthLongButton = document.getElementById('button-hair-length-long')
    const hairLengthMediumButton = document.getElementById('button-hair-length-medium')
    const hairLengthShortButton = document.getElementById('button-hair-length-short')

    function toggleService(button, price) {
        const priceText = document.getElementById('service-price')
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

        nextSlide.disabled = payload.services.length === 0;

        priceText.innerText = `Kokonaissumma: ${currentPrice}.00€`
    }

    serviceTypeBarberButton.addEventListener('click', (event) => {
        event.preventDefault()
        let checkmarks = document.getElementsByClassName('checkmark-service-type')
        Array.from(checkmarks).forEach((el) => {
            el.style.display = 'none'
        });
        serviceTypeBarberButton.nextElementSibling.style.display = "block"
        nextSlide.disabled = false
        payload.serviceType = "barber"
        delete payload.services
        currentPrice = 0

        const leea = document.getElementById('service-list')
            .getElementsByTagName('button')

        for (let i = 0; i < leea.length; i++) {
            leea[i].innerText = "+"
            leea[i].classList.remove('reservation-toggable-service-button-selected')
        }
        document.getElementById('service-price').innerText = 'Kokonaissumma: 00.00€'
    })

    serviceTypeHaidresserButton.addEventListener('click', (event) => {
        event.preventDefault()
        let checkmarks = document.getElementsByClassName('checkmark-service-type')
        Array.from(checkmarks).forEach((el) => {
            el.style.display = 'none'
        });
        serviceTypeHaidresserButton.nextElementSibling.style.display = "block"
        nextSlide.disabled = false
        payload.serviceType = "hairdresser"
        delete payload.services
        currentPrice = 0
        const leea = document.getElementById('service-list')
            .getElementsByTagName('button')

        for (let i = 0; i < leea.length; i++) {
            leea[i].innerText = "+"
            leea[i].classList.remove('reservation-toggable-service-button-selected')
        }
        document.getElementById('service-price').innerText = 'Kokonaissumma: 00.00€'
    })

    hairLengthLongButton.addEventListener('click', (event) => {
        event.preventDefault()
        let checkmarks = document.getElementsByClassName('checkmark-hair-length')
        Array.from(checkmarks).forEach((el) => {
            el.style.display = 'none'
        });
        hairLengthLongButton.nextElementSibling.style.display = "block"
        payload.hairLength = "long"
        nextSlide.disabled = false
    })

    hairLengthMediumButton.addEventListener('click', (event) => {
        event.preventDefault()
        let checkmarks = document.getElementsByClassName('checkmark-hair-length')
        Array.from(checkmarks).forEach((el) => {
            el.style.display = 'none'
        });
        hairLengthMediumButton.nextElementSibling.style.display = "block"
        payload.hairLength = "medium"
        nextSlide.disabled = false
    })

    hairLengthShortButton.addEventListener('click', (event) => {
        event.preventDefault()
        let checkmarks = document.getElementsByClassName('checkmark-hair-length')
        Array.from(checkmarks).forEach((el) => {
            el.style.display = 'none'
        });
        hairLengthShortButton.nextElementSibling.style.display = "block"
        payload.hairLength = "short"
        nextSlide.disabled = false
    })
})(window.reservationView = window.reservationView || {})

reservationView.start();