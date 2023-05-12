package Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

interface Cocinable {
	int getTiempoDeCoccion();
}

interface Calorico {
	int getCalorias();
}

class Receta {
	protected String nombre;
	protected String tipo;
	protected ArrayList<String> ingredientes;

	public Receta(String nombre, String tipo, ArrayList<String> ingredientes) {
		this.nombre = nombre;
		this.tipo = tipo;
		this.ingredientes = ingredientes;
	}

	public String getNombre() {
		return nombre;
	}

	public String getTipo() {
		return tipo;
	}

	public ArrayList<String> getIngredientes() {
		return ingredientes;
	}

	public int getTiempoDeCoccion() {
		return ingredientes.size();
	}

	public int getCalorias() {
		return ingredientes.size() * 3;
	}
}

class Salado extends Receta implements Cocinable, Calorico {
	public Salado(String nombre, ArrayList<String> ingredientes) {
		super(nombre, "Salado", ingredientes);
	}

}

class Dulce extends Receta implements Calorico {
	public Dulce(String nombre, ArrayList<String> ingredientes) {
		super(nombre, "Dulce", ingredientes);
	}
}

public class RecipeReader {
	public static void main(String[] args) {
		ArrayList<Receta> recetas = new ArrayList<Receta>();

		try {
			BufferedReader br = new BufferedReader(new FileReader("receta.txt"));
			String linea;
			String nombre = "";
			String tipo = "";
			ArrayList<String> ingredientes = new ArrayList<String>();

			while ((linea = br.readLine()) != null) {
				if (linea.startsWith("- Tipo de comida:")) {
					tipo = linea.substring(17);
				} else if (linea.startsWith("-")) {
					ingredientes.add(linea.substring(2));
				} else {
					if (!nombre.equals("")) {
						Receta receta;
						if (tipo.equals("Salado")) {
							receta = new Salado(nombre, ingredientes);
						} else {
							receta = new Dulce(nombre, ingredientes);
						}
						recetas.add(receta);
						nombre = "";
						tipo = "";
						ingredientes = new ArrayList<String>();
					}
					nombre = linea;
				}
			}
			Receta receta;
			if (tipo.equals("Salado")) {
				receta = new Salado(nombre, ingredientes);
			} else {
				receta = new Dulce(nombre, ingredientes);
			}
			recetas.add(receta);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileWriter fw = new FileWriter("Jenkinsfile");

			fw.write("pipeline {\n");
			fw.write("    agent any\n");
			fw.write("    stages {\n");

			for (Receta receta : recetas) {
				fw.write("        stage('" + receta.getNombre() + "') {\n");
				fw.write("            steps {\n");
				fw.write("                echo 'Tipo de comida: " + receta.getTipo() + "'\n");
				fw.write("                echo 'Ingredientes:'\n");
				for (String ingrediente : receta.getIngredientes()) {
					fw.write("                echo '- " + ingrediente + "'\n");
				}
				fw.write("                echo 'Tiempo de cocción: " + receta.getTiempoDeCoccion() + "'\n");
				fw.write(" echo 'Calorías: " + receta.getCalorias() + "'\n");
				fw.write(" }\n");
				fw.write(" }\n");
			}
			fw.write("    }\n");
			fw.write("}\n");

			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Jenkinsfile generado exitosamente.");
	}
}
