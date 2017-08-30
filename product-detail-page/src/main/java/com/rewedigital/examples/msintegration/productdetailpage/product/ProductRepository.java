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
				.withImage("25165635").build());

		result.put("0197892", new Product.Builder().withName("REWE Bio Frische Bio-Eier 6 Stück").withVendor("REWE Bio")
				.withPrice("€ 2,15").withProductNumber("0197892")
				.withDescription("Hergestellt für: REWE Markt GmbH, D-50603 Köln.").withImage("4777810").build());

		result.put("1028378", new Product.Builder().withName("REWE Beste Wahl Bananen").withVendor("REWE Beste Wahl")
				.withPrice("€ 0,34").withProductNumber("1028378")
				.withDescription(
						"Kl.: M; Ursprungsland und Klasse können je nach Angebot abweichen. Bananen werden grün geerntet, denn sie reifen nach. Die "
								+ "Büschel werden gewaschen, in \"Hände\" zerteilt und verpackt. Sie werden auf besonderen Bananenschiffen in die Verbraucherländer "
								+ "transportiert. Vorwiegend angebaut in Ecuador, Panama, Costa Rica, Kolumbien und Dominikanische Republik. Fleischig, aromatisch "
								+ "und je nach Reifegrad von leicht säuerlich bis sehr süß. Überwiegend Rohverzehr, Milchmixgetränke, aber auch Beilage zu würzigen "
								+ "Fleischgerichten, insbesondere mit Curry. Bananen vor Zugluft schützen! Die Früchte sind druckempfindlich. Bei zu kühlen Temperaturen"
								+ " färbt sich die Schale gelblich-grau bis schwarz und es kommt zu Aromaverlusten. Bananen sind leicht verdaulich und besonders für"
								+ " Kleinkinder sowie als Roh- und Diätkost geeignet. Sie liefern schnell Energie und stellen eine gute Zwischenmahlzeit auch für "
								+ "unterwegs dar. Die Krümmung der Banane entsteht während des Wachstums hormonbedingt.")
				.withImage("21012012").build());

		result.put("0197877", new Product.Builder().withName("REWE Bio Fettarme H-Milch 1,5% 1l").withVendor("REWE Bio")
				.withPrice("€ 1,05").withProductNumber("0197877")
				.withDescription("Hergestellt für: REWE Markt GmbH, D-50603 Köln.").withImage("1163210").build());

		result.put("0974662", new Product.Builder().withName("REWE Bio Schokomüsli 500g").withVendor("")
				.withPrice("€ 2,99").withProductNumber("0974662")
				.withDescription(
						"Das REWE Bio Müsli schmeckt herrlich getreidig. Ideal mit Milch, Joghurt oder pur als Knabberei.\n"
								+ "Naturland Getreide aus kontrolliert biologischem Anbau ist die Grundlage für das REWE Bio Müsli. Natürliche Düngung, bodenschonender "
								+ "Anbau und strenge Kontrollen nach Naturland Richtlinien sorgen für ein Müsli der Extraklasse. Diese Standards, die weit über die "
								+ "gesetzlichen Anforderungen der EU-Bio-Verordnung hinausgehen, garantieren Natürlichkeit und Geschmack. Genießen Sie den Unterschied!")
				.withImage("7891620").build());

		result.put("5883121", new Product.Builder().withName("ja! Markenbutter 250g").withVendor("ja!")
				.withPrice("€ 1,79").withProductNumber("5883121")
				.withDescription("Deutsche Markenbutter, Mildgesäuerte Butter, 82% Fett").withImage("5412060").build());

		result.put("0718568", new Product.Builder().withName("Van Nahmen Bio Aroniasaft 0,75l").withVendor("Van Nahmen")
				.withPrice("€ 4,69").withProductNumber("0718568")
				.withDescription(
						"Der Aroniastrauch stamm ursprünglich aus Nordamerika. Für unseren schonend gepressten, reinen Direktsaft verwenden wir ausschließlich frische "
								+ "Bio-Aroniabeeren. Der herbe Aroniasaft schmeckt hervorragend als Schorle sowie mit Apfelsaft oder Tee. Er ist reich an Polyphenolen.")
				.withImage("22812940").build());

		result.put("0321145", new Product.Builder().withName("REWE Bio Roggenbrötchen 6 Stück").withVendor("REWE Bio")
				.withPrice("€ 1,59").withProductNumber("0321145")
				.withDescription("Hergestellt für: REWE Markt GmbH, D-50603 Köln.").withImage("3515050").build());

		result.put("4925190",
				new Product.Builder().withName("Homann Feiner Nudelsalat 400g").withVendor("Homann").withPrice("€ 1,99")
						.withProductNumber("4925190").withDescription("Feiner Nudelsalat mit Schinkenwurst & Ei")
						.withImage("2323180").build());

		result.put("3066450",
				new Product.Builder().withName("Maggi Instant Klare Gemüsebrühe 7l").withVendor("Maggi")
						.withPrice("€ 1,89").withProductNumber("3066450").withDescription("Gemüsebrühe.")
						.withImage("26034526").build());
		return result;
	}
}