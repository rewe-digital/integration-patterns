package com.rewedigital.examples.msintegration.productdetailpage.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

import static java.lang.String.format;

@Controller
public class ProductWebController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductWebController.class);
    private final JpaProductRepository products;

    @Autowired
    public ProductWebController(final JpaProductRepository products) {
        this.products = products;
    }

    @GetMapping("/products/{productId}")
    public ModelAndView productDetails(@PathVariable("productId") final String productId,
            HttpServletResponse response) {
        LOGGER.info("GET /products/{}", productId);
        ModelAndView result = getProductDetails(productId);
        response.addHeader("x-uic-stylesheet", "/products/css/core.css");
        return result;
    }

    @GetMapping("/products")
    public ModelAndView productList(HttpServletResponse response) {
        ModelAndView result = getProductList();
        response.addHeader("x-uic-stylesheet", "/products/css/core.css");
        return result;
    }

    private ModelAndView getProductList() {
        ModelAndView result = new ModelAndView("productList");
        result.addObject("products", new ArrayList<>(products.findAll()));
        return result;
    }

    private ModelAndView getProductDetails(final String productId) {
        return products.findByProductNumber(productId).map(p -> {
            final ModelAndView result = new ModelAndView("product");
            result.addObject("product", p);
            return result;
        }).orElseThrow(() -> productNotFound(productId));
    }

    private RuntimeException productNotFound(final String id) {
        throw new RuntimeException(format("product [%s] not found", id));
    }

}
