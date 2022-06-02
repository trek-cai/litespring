package org.litespring.beans.factory.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.litespring.beans.BeanDefinition;
import org.litespring.beans.PropertyValue;
import org.litespring.beans.factory.BeanDefinitionStoreException;
import org.litespring.beans.factory.config.RuntimeBeanReference;
import org.litespring.beans.factory.config.TypedStringValue;
import org.litespring.beans.factory.support.GenericBeanDefinition;
import org.litespring.beans.factory.support.BeanDefinitionRegistry;
import org.litespring.core.io.Resource;
import org.litespring.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class XmlBeanDefinitionReader {

    public static final String ID_ATTRIBUTE = "id";
    public static final String CLASS_ATTRIBUTE = "class";
    public static final String SCORE_ATTRIBUTE = "score";
    public static final String PROPERTIES_ATTRIBUTE = "property";
    public static final String REF_ATTRIBUTE = "ref";
    public static final String VALUE_ATTRIBUTE = "value";
    public static final String NAME_ATTRIBUTE = "name";

    private BeanDefinitionRegistry registry;

    protected final Log logger = LogFactory.getLog(getClass());

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public void loadBeanDefinitions(Resource resource) {
        InputStream is = null;
        Document doc = null;
        try {
            is = resource.getInputStream();
            SAXReader reader = new SAXReader();
            doc = reader.read(is);
            Element root = doc.getRootElement();
            Iterator<Element> iter = root.elementIterator();
            while(iter.hasNext()) {
                Element element = iter.next();
                String beanID = element.attributeValue(ID_ATTRIBUTE);
                String beanClassName = element.attributeValue(CLASS_ATTRIBUTE);
                String beanScore = element.attributeValue(SCORE_ATTRIBUTE);
                BeanDefinition bd = new GenericBeanDefinition(beanID, beanClassName);
                if(beanScore != null) {
                    bd.setScore(beanScore);
                }
                parsePropertyElement(element, bd);
                registry.registerBeanDefinition(beanID, bd);
            }
        } catch (Exception e) {
            throw new BeanDefinitionStoreException("IOException parsing XML document from " + resource.getDescription(),e);
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void parsePropertyElement(Element beanElement, BeanDefinition bd) {
        Iterator iter = beanElement.elementIterator(PROPERTIES_ATTRIBUTE);
        while(iter.hasNext()) {
            Element propertyElem = (Element) iter.next();
            String propertyName = propertyElem.attributeValue(NAME_ATTRIBUTE);
            if (!StringUtils.hasLength(propertyName)) {
                logger.fatal("Tag 'property' must have a 'name' attribute");
                return;
            }
            Object val = parsePropertyValue(propertyElem, bd, propertyName);
            PropertyValue pv = new PropertyValue(propertyName, val);

            bd.getPropertyValues().add(pv);
        }
    }

    private Object parsePropertyValue(Element propertyElem, BeanDefinition bd, String propertyName) {
        String elementName = (propertyName != null) ?
                "<property> element for property '" + propertyName + "'" :
                "<constructor-arg> element";

        boolean hasRefAttribute = (propertyElem.attribute(REF_ATTRIBUTE)!=null);
        boolean hasValueAttribute = (propertyElem.attribute(VALUE_ATTRIBUTE) !=null);

        if(hasRefAttribute) {
            String refName = propertyElem.attributeValue(REF_ATTRIBUTE);
            if (!StringUtils.hasText(refName)) {
                logger.error(elementName + " contains empty 'ref' attribute");
            }
            RuntimeBeanReference ref = new RuntimeBeanReference(refName);
            return ref;
        } else if(hasValueAttribute) {
            TypedStringValue valueHolder = new TypedStringValue(propertyElem.attributeValue(VALUE_ATTRIBUTE));
            return valueHolder;
        } else {
            throw new RuntimeException(elementName + " must specify a ref or value");
        }
    }
}
