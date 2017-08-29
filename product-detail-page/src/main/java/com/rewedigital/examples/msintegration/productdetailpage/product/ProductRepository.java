package com.rewedigital.examples.msintegration.productdetailpage.product;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class ProductRepository {
	

	private static final Map<String, Product> allProducts = allProducts();

	public Collection<Product> list() {
		return allProducts.values();
	}

	public Optional<Product> get(final String id) {
		return Optional.ofNullable(allProducts.get(id));
	}

	private static Map<String, Product> allProducts() {
		final Map<String, Product> result = new HashMap<>();
		result.put("2670536", new Product.Builder().withName("Original Wagner Big Pizza Supreme 420g")
				.withVendor("Original Wagner").withPrice("€ 2,99").withProductNumber("2670536")
				.withDescription(
						"Big Taste, Big Fun and Big Pizza! Erlebe unsere großartige Kombination aus außergewöhnlichen Topping & Boden mit dem Big Crunch.\r\n"
								+ "\r\n"
								+ "Entdecke die Wagner Big Pizza Supreme: luftig-locker und herzhaft-saftig, belegt mit feinstem Mozzarella, "
								+ "leckeren Champignons, naturgereifter Edel-Salami, echtem gekochten Schinken und sonnengereiften Tomaten. Unser Geheimrezept für "
								+ "diesen Supreme-Genuss lautet: guter Teig, viel Belag und unser traditionelles Original Wagner Backverfahren. So entsteht die "
								+ "großartige Kombination aus herzhaft-saftigem Belag und dem luftig-lockeren Boden mit dem einzigartigen Big Crunch. Diese Big Pizza "
								+ "von Wagner macht ihrem Namen alle Ehre; eine Wagner Pizza, die Du im Haus haben solltest, wenn der große Hunger kommt oder ein paar "
								+ "gute Freunde! " + "- mit extra viel Käse " + "- extra reichhaltig belegt "
								+ "- großartige Kombination aus naturgereifter Salami, saftigem Kochschinken und leckeren Champignons\r\n"
								+ "- luftig-lockerer Teig mit dem Big Crunch")
				.withImage("25165635_digital-image.png").build());
		return result;
	}
}