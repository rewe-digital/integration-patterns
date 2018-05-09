#!/usr/bin/env bash
curl -d '{
	"name":"Original Wagner Big Pizza Supreme 420g",
	"vendor":"Original Wagner",
	"price":"€ 2,99",
	"description":"Big Taste, Big Fun and Big Pizza! Erlebe unsere großartige Kombination aus außergewöhnlichen Topping & Boden mit dem Big Crunch.Entdecke die Wagner Big Pizza Supreme: luftig-locker und herzhaft-saftig, belegt mit feinstem Mozzarella, leckeren Champignons, naturgereifter Edel-Salami, echtem gekochten Schinken und sonnengereiften Tomaten. Unser Geheimrezept für diesen Supreme-Genuss lautet: guter Teig, viel Belag und unser traditionelles Original Wagner Backverfahren. So entsteht die großartige Kombination aus herzhaft-saftigem Belag und dem luftig-lockeren Boden mit dem einzigartigen Big Crunch. Diese Big Pizza von Wagner macht ihrem Namen alle Ehre; eine Wagner Pizza, die Du im Haus haben solltest, wenn der große Hunger kommt oder ein paar gute Freunde! - mit extra viel Käse - extra reichhaltig belegt - großartige Kombination aus naturgereifter Salami, saftigem Kochschinken und leckeren Champignons\r\n - luftig-lockerer Teig mit dem Big Crunch", 
	"productNumber":"2670536",
	"image":"25165635"
	}' -H "Content-Type: application/json" -X POST http://localhost:9082/products &&
curl -d '{
	"name":"REWE Bio Frische Bio-Eier 6 Stück",
	"vendor":"REWE Bio",
	"price":"€ 2,15",
	"description":"Hergestellt für: REWE Markt GmbH, D-50603 Köln.", 
	"productNumber":"0197892",
	"image":"4777810"
	}' -H "Content-Type: application/json" -X POST http://localhost:9082/products &&
curl -d '{
	"name":"REWE Beste Wahl Bananen",
	"vendor":"REWE",
	"price":"€ 0,34",
	"description":"Kl.: M; Ursprungsland und Klasse können je nach Angebot abweichen. Bananen werden grün geerntet, denn sie reifen nach. Die Büschel werden gewaschen, in Hände zerteilt und verpackt. Sie werden auf besonderen Bananenschiffen in die Verbraucherländer transportiert. Vorwiegend angebaut in Ecuador, Panama, Costa Rica, Kolumbien und Dominikanische Republik. Fleischig, aromatisch und je nach Reifegrad von leicht säuerlich bis sehr süß. Überwiegend Rohverzehr, Milchmixgetränke, aber auch Beilage zu würzigen färbt sich die Schale gelblich-grau bis schwarz und es kommt zu Aromaverlusten. Bananen sind leicht verdaulich und besonders für Kleinkinder sowie als Roh- und Diätkost geeignet. Sie liefern schnell Energie und stellen eine gute Zwischenmahlzeit auch für unterwegs dar. Die Krümmung der Banane entsteht während des Wachstums hormonbedingt.",
	"productNumber":"1028378",
	"image":"4985655"
	}' -H "Content-Type: application/json" -X POST http://localhost:9082/products &&
curl -d '{
	"name":"REWE Bio Fettarme H-Milch 1,5% 1l",
	"vendor":"REWE Bio",
	"price":"€ 1,05",
	"description":"Das REWE Bio Müsli schmeckt herrlich getreidig. Ideal mit Milch, Joghurt oder pur als Knabberei.\n Naturland Getreide aus kontrolliert biologischem Anbau ist die Grundlage für das REWE Bio Müsli. Natürliche Düngung, bodenschonender Anbau und strenge Kontrollen nach Naturland Richtlinien sorgen für ein Müsli der Extraklasse. Diese Standards, die weit über die gesetzlichen Anforderungen der EU-Bio-Verordnung hinausgehen, garantieren Natürlichkeit und Geschmack. Genießen Sie den Unterschied!", 
	"productNumber":"0197877",
	"image":"1163210"
	}' -H "Content-Type: application/json" -X POST http://localhost:9082/products &&
curl -d '{
	"name":"REWE Bio Schokomüsli 500g",
	"vendor":"REWE",
	"price":"€ 2,99",
	"description":"Das REWE Bio Müsli schmeckt herrlich getreidig. Ideal mit Milch, Joghurt oder pur als Knabberei.\n Naturland Getreide aus kontrolliert biologischem Anbau ist die Grundlage für das REWE Bio Müsli. Natürliche Düngung, bodenschonender  Anbau und strenge Kontrollen nach Naturland Richtlinien sorgen für ein Müsli der Extraklasse. Diese Standards, die weit über die gesetzlichen Anforderungen der EU-Bio-Verordnung hinausgehen, garantieren Natürlichkeit und Geschmack. Genießen Sie den Unterschied!", 
	"productNumber":"0974662",
	"image":"7891620"
	}' -H "Content-Type: application/json" -X POST http://localhost:9082/products &&
curl -d '{
	"name":"ja! Markenbutter 250g",
	"vendor":"ja!",
	"price":"€ 1,79",
	"description":"Deutsche Markenbutter, Mildgesäuerte Butter, 82% Fett", 
	"productNumber":"5883121",
	"image":"5412060"
	}' -H "Content-Type: application/json" -X POST http://localhost:9082/products &&
curl -d '{
	"name":"Van Nahmen Bio Aroniasaft 0,75l",
	"vendor":"Van Nahmen",
	"price":"€ 4,69",
	"description":"Der Aroniastrauch stamm ursprünglich aus Nordamerika. Für unseren schonend gepressten, reinen Direktsaft verwenden wir ausschließlich frische Bio-Aroniabeeren. Der herbe Aroniasaft schmeckt hervorragend als Schorle sowie mit Apfelsaft oder Tee. Er ist reich an Polyphenolen.", 
	"productNumber":"0718568",
	"image":"22812940"
	}' -H "Content-Type: application/json" -X POST http://localhost:9082/products &&
curl -d '{
	"name":"REWE Bio Roggenbrötchen 6 Stück",
	"vendor":"REWE Bio",
	"price":"€ 1,59",
	"description":"Hergestellt für: REWE Markt GmbH, D-50603 Köln.", 
	"productNumber":"0321145",
	"image":"3515050"
	}' -H "Content-Type: application/json" -X POST http://localhost:9082/products &&
curl -d '{
	"name":"Homann Feiner Nudelsalat 400g",
	"vendor":"Homann",
	"price":"€ 1,99",
	"description":"Feiner Nudelsalat mit Schinkenwurst & Ei", 
	"productNumber":"4925190",
	"image":"2323180"
	}' -H "Content-Type: application/json" -X POST http://localhost:9082/products &&
curl -d '{
	"name":"Maggi Instant Klare Gemüsebrühe 7l",
	"vendor":"Maggi",
	"price":"€ 1,89",
	"description":"Gemüsebrühe.", 
	"productNumber":"3066450",
	"image":"26034526"
	}' -H "Content-Type: application/json" -X POST http://localhost:9082/products