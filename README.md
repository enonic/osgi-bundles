# Enonic OSGi Bundles

This project build OSGi wrappers of some libraries we use here at Enonic. The following libraries are OSGi wrapped:

* AttoParser 1.3 (org.attoparser/attoparser)
* Elasticsearch 1.7.4 (org.elasticsearch/elasticsearch)
* Resteasy 3.0.8 (org.jboss.resteasy/resteasy-jaxrs)
* OKHttp 2.5.0 (com.squareup.okhttp/okhttp)

## Building

To install all libraries locally (in your own local Maven repositroy):

    ./gradlew clean install

If you want to deploy all libraries in Enonic's remote repository:

    ./gradlew clean publish
