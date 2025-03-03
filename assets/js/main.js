jQuery(function () {
    "use strict";

    // Function to handle the spinner
    var spinner = function () {
        setTimeout(function () {
            if ($('#spinner').length > 0) {
                $('#spinner').removeClass('show');
            }
        }, 500); // Adding a slight delay before hiding spinner
    };
    spinner();

    // Sticky Navbar on scroll
    $(window).scroll(function () {
        if ($(this).scrollTop() > 45) {
            $('.navbar').addClass('sticky-top shadow-sm');
        } else {
            $('.navbar').removeClass('sticky-top shadow-sm');
        }
    });

    // International Tour carousel
    $(".InternationalTour-carousel").owlCarousel({
        autoplay: true,
        smartSpeed: 1000,
        center: false,
        dots: true,
        loop: true,
        margin: 25,
        nav: false,
        navText: [
            '<i class="bi bi-arrow-left"></i>',
            '<i class="bi bi-arrow-right"></i>'
        ],
        responsiveClass: true,
        responsive: {
            0: { items: 1 },
            768: { items: 2 },
            992: { items: 2 },
            1200: { items: 3 }
        }
    });

    // Packages carousel
    $(".packages-carousel").owlCarousel({
        autoplay: true,
        smartSpeed: 1000,
        center: false,
        dots: false,
        loop: true,
        margin: 25,
        nav: true,
        navText: [
            '<i class="bi bi-arrow-left"></i>',
            '<i class="bi bi-arrow-right"></i>'
        ],
        responsiveClass: true,
        responsive: {
            0: { items: 1 },
            768: { items: 2 },
            992: { items: 2 },
            1200: { items: 3 }
        }
    });

    // Testimonial carousel
    $(".testimonial-carousel").owlCarousel({
        autoplay: true,
        smartSpeed: 1000,
        center: true,
        dots: true,
        loop: true,
        margin: 25,
        nav : true,
        navText : [
            '<i class="bi bi-arrow-left"></i>',
            '<i class="bi bi-arrow-right"></i>'
        ],
        responsiveClass: true,
        responsive: {
            0: { items: 1 },
            768: { items: 2 },
            992: { items: 2 },
            1200: { items: 3 }
        }
    });

    // Open cart modal
    $('#cartButton').click(function () {
        $('#cartModal').modal('show');
    });

    // Handle Add to Cart (AJAX)
    $('.add-to-cart-btn').on('click', function (event) {
        event.preventDefault();

        const productId = $(this).data('product-id');
        const url = $(this).attr('href'); // URL to add product to cart

        $.ajax({
            url: url,
            method: 'GET',
            success: function (response) {
                if (response.success) {
                    // Update the cart in the modal
                    updateCart(response.cart);
                    alert('Product added to cart!');
                } else {
                    alert(response.message); // Show error message if any
                }
            },
            error: function () {
                alert('An error occurred while adding the product to the cart.');
            }
        });
    });

    // Handle Update Cart Quantity (AJAX)
    $(document).on('click', '.update-cart-btn', function (event) {
        event.preventDefault();

        const productId = $(this).data('product-id');
        const action = $(this).data('action'); // Increase or Decrease
        const url = `/cart/update/${productId}`; // Adjust this path if necessary

        $.ajax({
            url: url,
            method: 'POST',
            data: { action: action },
            success: function (response) {
                if (response.success) {
                    // Update the cart in the modal
                    updateCart(response.cart);
                }
            },
            error: function () {
                alert('An error occurred while updating the cart.');
            }
        });
    });

    // Handle Remove from Cart (AJAX)
    $(document).on('click', '.remove-from-cart-btn', function (event) {
        event.preventDefault();

        const productId = $(this).data('product-id');
        const url = $(this).attr('href'); // Get the URL to remove item

        $.ajax({
            url: url,
            method: 'GET',
            success: function (response) {
                if (response.success) {
                    // Update the cart in the modal
                    updateCart(response.cart);
                }
            },
            error: function () {
                alert('An error occurred while removing the product from the cart.');
            }
        });
    });

    // Function to update the cart dynamically in the modal
    function updateCart(cart) {
        let cartHtml = '';
        cart.items.forEach(item => {
            cartHtml += `
                <tr>
                    <td>${item.product}</td>
                    <td>${item.quantity}</td>
                    <td>${item.price}€</td>
                    <td>${item.total}€</td>
                    <td>
                        <button class="update-cart-btn" data-product-id="${item.product}" data-action="increase">+</button>
                        <button class="update-cart-btn" data-product-id="${item.product}" data-action="decrease">-</button>
                        <button class="remove-from-cart-btn" data-product-id="${item.product}">Remove</button>
                    </td>
                </tr>
            `;
        });

        $('#cart-items').html(cartHtml); // Assuming your cart items are in an element with the id "cart-items"
        $('#cart-total').html(cart.total); // Update the cart total
    }

    // Back to top button
    $(window).scroll(function () {
        if ($(this).scrollTop() > 300) {
            $('.back-to-top').fadeIn('slow');
        } else {
            $('.back-to-top').fadeOut('slow');
        }
    });

    $('.back-to-top').click(function () {
        $('html, body').animate({ scrollTop: 0 }, 1500, 'easeInOutExpo');
        return false;
    });

});
