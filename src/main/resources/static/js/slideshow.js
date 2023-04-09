(function(slideshow) {
    'use strict';

    const pi = {};

    const slideState = {
        currentSlide: 0,
        slideCount: 0,
    };

    const onSlidePageChange = [];

    slideshow.initialize = function () {
        pi.slides = document.querySelectorAll('.slide');
        pi.nextSlideButton = document.querySelector('.slide-arrow-next');
        pi.previousSlideButton = document.querySelector(".slide-arrow-prev");

        slideState.slideCount = pi.slides.length - 1;

        for (let i = 1; i < pi.slides.length; i++) {
            pi.slides[i].style.display = 'none'
        }

        pi.previousSlideButton.style.visibility = 'hidden';

        pi.nextSlideButton.addEventListener('click', onNextSlideButtonClick);
        pi.previousSlideButton.addEventListener('click', onPrevSlideButtonClick)

        return { slideState, onSlidePageChange, nextSlideButton: pi.nextSlideButton, previousSlideButton: pi.previousSlideButton };
    }

    function onNextSlideButtonClick(event) {
        event.preventDefault();
        window.scrollTo(0, 0)
        if(slideState.currentSlide + 1 === slideState.slideCount) {
            pi.nextSlideButton.style.visibility = 'hidden';
        }

        if (slideState.currentSlide === slideState.slideCount) {
            return;
        } else {
            slideState.currentSlide++;
        }

        pi.previousSlideButton.style.visibility = 'visible';

        for (let i = 0; i < pi.slides.length; i++) {
            if (i === slideState.currentSlide) {
                pi.slides[i].style.display = 'block'
            } else {
                pi.slides[i].style.display = 'none'
            }
        }

        onSlidePageChangeTrigger();
    }


    function onPrevSlideButtonClick(event) {
        event.preventDefault();
        window.scrollTo(0, 0)
        if (slideState.currentSlide <= 1) {
            pi.previousSlideButton.style.visibility = 'hidden'
        }

        if (slideState.currentSlide === 0) {
            return;
        } else {
            slideState.currentSlide--;
        }

        pi.nextSlideButton.style.visibility = 'visible';

        for (let i = 0; i < pi.slides.length; i++) {
            if (i === slideState.currentSlide) {
                pi.slides[i].style.display = 'block'
            } else {
                pi.slides[i].style.display = 'none'
            }
        }

        onSlidePageChangeTrigger();
    }

    function onSlidePageChangeTrigger() {
        for(const element of onSlidePageChange) {
            element();
        }
    }
})(window.slideshow = window.slideshow || {});