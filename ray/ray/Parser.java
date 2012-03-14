package ray;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ray.math.Color;
import ray.math.Tuple3;

/**
 * Simple XML based scene parser.  The parser works recursively on the
 * nodes of the XML file.  Each node will be interpreted as a Java Object.
 * The type of each node is inferred from the nodes name and the class
 * type of the parent node (the class type of the root node
 * is always assumed to be Scene).  If <name> is the name of
 * the node, the class type of the parent node is searched for methods
 * named, first, set<name> and then add<name>.  If no such method is found
 * or if the found method does not take exactly one parameter, parsing
 * fails.  If a valid method is found, the type of the node is assumed
 * to be the type of the parameter of the found method.
 *
 * After the node is parsed, the method found in the parent object is
 * called with the parsed value of the node as the only argument.  It
 * is possible to specify that class type of the parsed node be a subclass
 * of the automatically determined class type by setting the "type"
 * attribute in the node.  The value of the type node must be the fully 
 * qualified name for class (i.e. ray.surface.Sphere) Additionally, nodes 
 * can be named with the "name" attribute.  After they are added to the 
 * parent, named nodes are added to a hash table internal to the parser.  
 * Later, other nodes can reference a named node by using a "ref" attribute.  
 * The value of the  reference attribute is used to index in the hash table 
 * and the previously stored node with the matching name is used instead of
 * a parsed value.
 *
 * There are special routines for explicitly parsing primitives,
 * arrays of primitives, Tuple3 (either Vector3 or Point3), Colors, Strings,
 * Images, and Meshes.  If a node is found, as above, to describe any of
 * these types a special method is used instead of the above
 * recursive procedure.
 *
 * An example is, hopefully, even clearer.  Consider a simple example
 * input:
 *
 * <scene>
 *    <camera>
 *      <eye> 1 1 1 </eye>
 *    </camera>
 *    <material name="white" type="ray1.material.Lambertian">
 *      <diffuseColor>1 1 1</diffuseColor>
 *    </material>
 *    <surface type="ray1.surface.Sphere">
 *      <material ref="white"/>
 *      <center>1 1 1</center>
 *      <radius>1</radius>
 *    </surface>
 * </scene>
 *
 * This would be parsed as follows:
 *
 *  -- The scene tag is automatically supplied the type Scene
 *  -- The camera tag is read, and the setCamera method is
 *     found in the Scene class.
 *  -- Set camera takes a Camera as an argument, so the <camera>
 *     node is parsed as a Camera object
 *  -- The <eye> node is processed similarly, finding setEye
 *     in camera, but setEye takes a Vector3 as an argument
 *     so its value is parsed directly as a Vector3 (whitespace
 *     delimited)
 *  -- After <eye> is processed, setEye(eye) is called in Camera
 *  -- Then setCamera(camera) is called on Scene
 *  -- The <material> tag is processed in the same fashion, but
 *     the type attribute specifies that it be parsed additionally
 *     as a Lambertian material.
 *  -- The name attribute in the material tag stores it for
 *     later use in the <surface> tag.  The <material>
 *     tag in the <surface> tag is not parsed, but the same object
 *     produced by parsing the above "white" material is supplied
 *     instead.
 *
 * The parser is completely generic and can be used with new classes
 * that you write for your project.  There are only two limitations.
 * First, every object used as a node must have a zero argument constructor
 * so the parser an instantiate an empty object to add elements too.
 * Second, every node that will have children must have setXXX or addXXX
 * methods for each child type and they must take exactly one parameter
 * of the type of the child node.
 *
 * @author arbree Aug 18, 2005
 * Parser.java
 * Copyright 2005 Program of Computer Graphics, Cornell University
 */
public class Parser {

	/** Java document builder used to parse XML * */
	private DocumentBuilder db;

	/** Map of references to their names **/
	public static HashMap<String, Object> references = new HashMap<String, Object>();

	/** Creates a new Parser. */
	public Parser() {

		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (Exception e) {

			throw new Error("Error instantiating the parser.");

		}
	}

	/**
	 * Parses the String and generates either an Integer object or a Double
	 * object, depending on the given class.
	 *
	 * @param c the class to parse the string as
	 * @param text the text to parse
	 * @return a new object of the given type
	 */
	@SuppressWarnings("rawtypes")
	private Object parsePrimitive(Class c, String text) {

		if (c == Integer.TYPE) {
			return new Integer(text);
		} else if (c == Double.TYPE) {
			return new Double(text);
		} else {
			throw new Error("Cannot parse primitive of type " + c);
		}
	}

	/**
	 * Parse an array
	 *
	 * @param componentType the type of each component in the array
	 * @param text the text in the node to be parsed
	 * @return an ArrayList of temporary values used to create the array at the
	 *         return point
	 */
	@SuppressWarnings("rawtypes")
	private ArrayList<Object> parseArray(Class componentType, String text) {

		StringTokenizer t = new StringTokenizer(text);
		ArrayList<Object> result = new ArrayList<Object>();
		while (t.hasMoreTokens()) {
			String token = t.nextToken();
			result.add(parsePrimitive(componentType, token));
		}
		return result;
	}

	/**
	 * Parse some special types of objects
	 *
	 * @param c the class type to parse
	 * @param text the text to interpret as an object
	 * @return the object parsed
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object parseObject(Class c, String text) {

		if (c == String.class) {
			return text;
		}

		else if (c == Integer.class) {
			return new Integer(text);
		}

		else if (c == Double.class) {
			return new Double(text);
		}

		else if ((c.isArray() && c.getComponentType().isPrimitive())) {
			ArrayList tempArray = parseArray(c.getComponentType(), text);
			Object result = Array.newInstance(c.getComponentType(), tempArray
					.size());
			for (int i = 0; i < tempArray.size(); i++) {
				Array.set(result, i, tempArray.get(i));
			}
			return result;
		}

		else if (Tuple3.class.isAssignableFrom(c)) {
			ArrayList tempArray = parseArray(Double.TYPE, text);
			if (tempArray.size() != 3) {
				throw new Error("Tuple3 is not of length 3 ("
						+ tempArray.size() + ")");
			}
			Tuple3 result;
			try {
				result = (Tuple3) c.newInstance();
			} catch (Exception e) {
				throw new Error("Error instantiating object of class: "
						+ c.getName());
			}
			result.x = ((Double) tempArray.get(0)).doubleValue();
			result.y = ((Double) tempArray.get(1)).doubleValue();
			result.z = ((Double) tempArray.get(2)).doubleValue();
			return result;
		}

		else if (Color.class.isAssignableFrom(c)) {
			ArrayList tempArray = parseArray(Double.TYPE, text);
			if (tempArray.size() != 3) {
				throw new Error("Color is not of length 3 (" + tempArray.size()
						+ ")");
			}
			Color result;
			try {
				result = (Color) c.newInstance();
			} catch (Exception e) {
				throw new Error("Error instantiating object of class: "
						+ c.getName());
			}
			result.r = ((Double) tempArray.get(0)).doubleValue();
			result.g = ((Double) tempArray.get(1)).doubleValue();
			result.b = ((Double) tempArray.get(2)).doubleValue();
			return result;
		}

		else if (Image.class.isAssignableFrom(c)) {
			ArrayList tempArray = parseArray(Integer.TYPE, text);
			if (tempArray.size() != 2) {
				throw new Error(
						"Image declaration specifies only width and height");
			}
			Image result;
			try {
				Constructor imageConstructor = c.getConstructor(new Class[] {
						Integer.TYPE, Integer.TYPE });
				result = (Image) imageConstructor
						.newInstance(new Object[] { (Integer) tempArray.get(0),
								(Integer) tempArray.get(1) });
			} catch (Exception e) {
				throw new Error("Error instantiating object of class: "
						+ c.getName());
			}
			return result;
		}

		else {
			throw new Error("Cannot parse type: " + c);
		}
	}

	/**
	 * Return the Method object representing the string methodName in Class c.
	 * Return null if no such method if found.
	 *
	 * @param c the class to find a method in
	 * @param methodName the name of the method to find
	 * @return the Method object if a method is found, null otherwise
	 */
	@SuppressWarnings("rawtypes")
	private Method findMethod(Class c, String methodName) {

		// Get the method list
		Method[] methods = c.getMethods();
		for (int j = 0; j < methods.length; j++) {
			Method m = methods[j];
			if (methodName.equalsIgnoreCase(m.getName())) {
				return m;
			}
		}
		return null;

	}

	/**
	 * Parse an object node. The node is assumed to be of Class c and is
	 * represented by Node n.
	 *
	 * @param c Class type to read from node n
	 * @param n the node to parse into an instance of c
	 * @return the object read
	 */
	@SuppressWarnings("rawtypes")
	private Object parseObject(Class<?> c, Node n) {

		Object resultingObject = null;
		NamedNodeMap attributes = n.getAttributes();
		Node typeAttribute = attributes.getNamedItem("type");
		Node nameAttribute = attributes.getNamedItem("name");
		Node refAttribute = attributes.getNamedItem("ref");

		if (typeAttribute != null) {
			String className = typeAttribute.getNodeValue();
			try {
				Class<?> possibleClass;
				// Try to look up the class; if it fails, try looking in the package
				// of the given base type
				try {
					possibleClass = Class.forName(className);
				} catch (ClassNotFoundException e) {
					possibleClass = Class.forName(c.getPackage().getName()
							+ "." + className);
				}
				if (c.isAssignableFrom(possibleClass)) {
					c = possibleClass; // Set the current active class to user specified type
				} else {
					throw new Error("Type " + className
							+ " does not extend or implement " + c.getName());
				}
			} catch (ClassNotFoundException e) {
				throw new Error("Class could not be found: " + className);
			}
		}

		// If the node specifies a type, check that it is assignable to the current
		// output type for this node
		if (typeAttribute != null) {
			String className = typeAttribute.getNodeValue();
			try {
				Class<?> possibleClass;
				// Try to look up the class; if it fails, try looking in the package
				// of the given base type
				try {
					possibleClass = Class.forName(className);
				} catch (ClassNotFoundException e) {
					possibleClass = Class.forName(c.getPackage().getName()
							+ "." + className);
				}
				if (c.isAssignableFrom(possibleClass)) {
					c = possibleClass; // Set the current active class to user specified type
				} else {
					throw new Error("Type " + className
							+ " does not extend or implement " + c.getName());
				}
			} catch (ClassNotFoundException e) {
				throw new Error("Class could not be found: " + className);
			}
		}

		// Check that our current type is valid
		if (c.isArray() && !c.getComponentType().isPrimitive()) {
			throw new Error("Cannot parse arrays of non-primitive types");
		}

		// Get the sub elements of this node
		NodeList children = n.getChildNodes();

		//If the object is a reference, just return the value referenced
		if (refAttribute != null) {
			String name = refAttribute.getNodeValue();
			resultingObject = references.get(name);
			if (resultingObject == null) {
				throw new Error("Unresolved reference: " + name);
			}
		}

		// Check for certain special classes of the current node
		else if ((c.isArray() && c.getComponentType().isPrimitive())
				|| c == String.class || c == Integer.class || c == Double.class
				|| c == Color.class || Tuple3.class.isAssignableFrom(c)
				|| Image.class.isAssignableFrom(c)) {

			// Interpret the text values of all children nodes as objects
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.TEXT_NODE) {
					resultingObject = parseObject(c, child.getNodeValue());
				} else {
					throw new Error(
							"Found a non-text node while trying to parse a "
									+ c.getName());
				}
			}
		}

		// Otherwise the node represents a general object
		else {

			// Create one!
			try {
				resultingObject = c.newInstance();
			} catch (Exception e) {
				throw new Error("Error instantiating object of class: "
						+ c.getName());
			}

			// For each child
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);

				// Skip non-element nodes
				if (child.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}

				//Find the method to use for adding
				Method foundMethod = null;
				String childName = child.getNodeName();

				// Check setXXX
				foundMethod = findMethod(c, "set" + childName);

				// If can't find setXXX method, look for addXXX method instead
				if (foundMethod == null)
					foundMethod = findMethod(c, "add" + childName);

				// Its an error if no method was found
				if (foundMethod == null) {
					throw new Error("Could not find a method to use to add "
							+ childName + " to the class type " + c.getName()
							+ ".");
				}

				// Check that the method has the right number of parameters
				Class[] parameterTypes = foundMethod.getParameterTypes();
				if (parameterTypes.length != 1) {
					throw new Error("Method " + foundMethod.getName()
							+ " must take exactly one parameter.");
				}

				// If the type is primitive, switch to corresponding Object type
				// to parse. Method invocation will automatically take care of
				// converting Object types back into primitives.
				Class parameterType = parameterTypes[0];
				if (parameterType.isPrimitive()) {
					if (parameterType == Integer.TYPE) {
						parameterType = Integer.class;
					} else if (parameterType == Float.TYPE) {
						parameterType = Float.class;
					} else if (parameterType == Double.TYPE) {
						parameterType = Double.class;
					} else {
						throw new Error("Cannot parse primitives of type "
								+ parameterType);
					}
				}

				// Recursively parse value of child element
				Object childValue = parseObject(parameterType, child);

				// Call the setter method with the parsed value;
				try {

					//Invoke the setter method
					foundMethod.invoke(resultingObject,
							new Object[] { childValue });

				} catch (Exception e) {
					System.err.println("Error invoking the method "
							+ foundMethod.getName() + ".");
					e.printStackTrace();
				}
			}
		}

		//Place the object in the reference list
		if (nameAttribute != null) {
			String name = nameAttribute.getNodeValue();
			references.put(name, resultingObject);
		}

		return resultingObject;

	}

	/**
	 * Parses a given file to generate an object of the given class.
	 *
	 * @param filename the name of the XML file to parse
	 * @param c the class of the object to parse
	 * @return a new object of the given class
	 */
	public Object parse(String filename, Class<?> c) {

		File file = new File(filename);

		// Parse the XML
		Object result = null;
		try {

			Document doc = db.parse(file);
			Element root = doc.getDocumentElement();
			result = parseObject(c, root);

		} catch (Exception e) {
			System.out.println("Exception occurred while parsing: " + filename);
			e.printStackTrace();
		}

		return result;
	}

}
