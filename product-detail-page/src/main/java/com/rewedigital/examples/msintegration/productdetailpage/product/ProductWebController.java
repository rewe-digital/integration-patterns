package com.rewedigital.examples.msintegration.productdetailpage.product;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProductWebController {

    @GetMapping("/products/{productId}")
    public ModelAndView productDetails(@PathVariable("productId") final String productId) {
        final Product product = getProductById(productId);
        final ModelAndView result = new ModelAndView("product");
        result.addObject("product", product);
        return result;
    }

    private Product getProductById(String productId) {
        return new Product.Builder().withName("Original Wagner Big Pizza Supreme 420g").withVendor("Original Wagner")
                .withPrice("€ 2,99")
                .withProductNumber("2670536")
                .withDescription(
                        "Big Taste, Big Fun and Big Pizza! Erlebe unsere großartige Kombination aus außergewöhnlichen Topping & Boden mit dem Big Crunch.\r\n"
                                + "\r\n"
                                + "Entdecke die Wagner Big Pizza Supreme: luftig-locker und herzhaft-saftig, belegt mit feinstem Mozzarella, "
                                + "leckeren Champignons, naturgereifter Edel-Salami, echtem gekochten Schinken und sonnengereiften Tomaten. Unser Geheimrezept für "
                                + "diesen Supreme-Genuss lautet: guter Teig, viel Belag und unser traditionelles Original Wagner Backverfahren. So entsteht die "
                                + "großartige Kombination aus herzhaft-saftigem Belag und dem luftig-lockeren Boden mit dem einzigartigen Big Crunch. Diese Big Pizza "
                                + "von Wagner macht ihrem Namen alle Ehre; eine Wagner Pizza, die Du im Haus haben solltest, wenn der große Hunger kommt oder ein paar "
                                + "gute Freunde! " + "- mit extra viel Käse "
                                + "- extra reichhaltig belegt "
                                + "- großartige Kombination aus naturgereifter Salami, saftigem Kochschinken und leckeren Champignons\r\n"
                                + "- luftig-lockerer Teig mit dem Big Crunch")
                .withImage("25165635_digital-image.png").build();
    }

}
