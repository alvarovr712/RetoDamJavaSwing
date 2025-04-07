package org.example.com.tuempresa.swing;

import org.example.com.tuempresa.swing.model.Categoria;
import org.example.com.tuempresa.swing.model.Empresa;
import org.example.com.tuempresa.swing.model.Usuario;
import org.example.com.tuempresa.swing.model.Vacante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class AdministradorFrame extends JFrame{
    private ApiClient apiClient;
    private JPanel contentPanel;

    public AdministradorFrame(){
        setTitle("Administrador");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        //Crear un panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());

        //Crear un menu dentro del panel principal con el boxlayout.Y_AXIS le damos una direccion vertical
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel,BoxLayout.Y_AXIS));


        //Crear los botones de nuestro menú lateral
        JButton botonGestionEmpresa = new JButton("Gestion Empresa");
        botonGestionEmpresa.addActionListener(e -> mostrarGestionEmpresa());

        JButton botonGestionCategorias = new JButton("Gestion Categorias");
        botonGestionCategorias.addActionListener(e -> mostrarGestionCategorias());

        JButton botonGestionUsuario = new JButton("Gestion Usuario");
        botonGestionUsuario.addActionListener(e -> mostrarGestionUsuarios());

        JButton botonGestionAdministrador = new JButton("Gestion Administrador");
        botonGestionAdministrador.addActionListener(e -> mostrarGestionAdministradores());


        //Agregamos nuestros botones al menú

        menuPanel.add(botonGestionEmpresa);
        menuPanel.add(botonGestionCategorias);
        menuPanel.add(botonGestionUsuario);
        menuPanel.add(botonGestionAdministrador);


        //Darle un estilo a los botones
        for(Component item: menuPanel.getComponents()){
            if(item instanceof JButton){
                JButton button = (JButton) item;
                button.setFont(new Font("Arial",Font.PLAIN,16));
                //Centrar los botones
                button.setAlignmentX(Component.CENTER_ALIGNMENT);
                button.setMaximumSize(new Dimension(200,400));
            }
        }

        //Contenido principal del panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        JLabel label = new JLabel("Bienvenido al sistema !",SwingConstants.CENTER);
        contentPanel.add(label, BorderLayout.CENTER);


        //Con JSplitPane lo que hacemos es dar una separación en este caso entre menuPanel y contentPanel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,menuPanel,contentPanel);
        splitPane.setDividerLocation(250);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        add(mainPanel);

        setVisible(true);
        apiClient = new ApiClient();

    }


    // DESDE AQUI SE MANEJA EL CRUD DE GESTION EMPRESA
    private void mostrarGestionEmpresa(){
        // Limpiar el contenido actual
        contentPanel.removeAll();

        // Panel del submenu para CRUD (disposición horizontal)
        JPanel submenuPanel = new JPanel();
        submenuPanel.setLayout(new BoxLayout(submenuPanel, BoxLayout.X_AXIS));

        // Crear los botones para las operaciones CRUD
        JButton btnCrearEmpresa = new JButton("Crear");
        JButton btnLeerEmpresa = new JButton("Leer");
        JButton btnActualizarEmpresa = new JButton("Actualizar");
        JButton btnEliminarEmpresa = new JButton("Eliminar");

        // Establecer dimensiones preferidas y espaciado para los botones
        Dimension buttonDimension = new Dimension(400, 50);
        int espacio = 10;
        btnCrearEmpresa.setPreferredSize(buttonDimension);
        btnLeerEmpresa.setPreferredSize(buttonDimension);
        btnActualizarEmpresa.setPreferredSize(buttonDimension);
        btnEliminarEmpresa.setPreferredSize(buttonDimension);

        btnCrearEmpresa.setBorder(BorderFactory.createEmptyBorder(0, espacio, 0, espacio));
        btnLeerEmpresa.setBorder(BorderFactory.createEmptyBorder(0, espacio, 0, espacio));
        btnActualizarEmpresa.setBorder(BorderFactory.createEmptyBorder(0, espacio, 0, espacio));
        btnEliminarEmpresa.setBorder(BorderFactory.createEmptyBorder(0, espacio, 0, espacio));

        // Agregar los botones al panel de submenu
        submenuPanel.add(btnCrearEmpresa);
        submenuPanel.add(btnLeerEmpresa);
        submenuPanel.add(btnActualizarEmpresa);
        submenuPanel.add(btnEliminarEmpresa);

        // Crear un panel para el contenido que se mostrará debajo del submenu
        JPanel crudContentPanel = new JPanel(new BorderLayout());
        // Agregar un mensaje o componente inicial (placeholder)
        crudContentPanel.add(new JLabel("Área de contenido CRUD", SwingConstants.CENTER), BorderLayout.CENTER);

        // Configurar el contentPanel con BorderLayout:
        // - En el NORTH ubicamos el submenu (botones CRUD)
        // - En el CENTER ubicamos el panel de contenido dinámico
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(submenuPanel, BorderLayout.NORTH);
        contentPanel.add(crudContentPanel, BorderLayout.CENTER);

        // Definir action listeners para que cada botón actualice el panel de contenido
        btnCrearEmpresa.addActionListener(e -> {
                    crudContentPanel.removeAll();

                    // Panel para el formulario con GridBagLayout para una distribución ordenada
                    JPanel formPanel = new JPanel(new GridBagLayout());
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(10, 10, 10, 10);
                    gbc.fill = GridBagConstraints.HORIZONTAL;

                    // Crear etiquetas y campos de texto para cada dato

                    JLabel labelRazon = new JLabel("Razón Social:");
                    JTextField txtRazon = new JTextField(20);

                    JLabel labelDireccion = new JLabel("Dirección Fiscal:");
                    JTextField txtDireccion = new JTextField(20);

                    JLabel labelPais = new JLabel("País:");
                    JTextField txtPais = new JTextField(20);

                    // Agregar los componentes al formulario usando GridBagConstraints
                    gbc.gridx = 0;
                    gbc.gridy = 1;
                    formPanel.add(labelRazon, gbc);
                    gbc.gridx = 1;
                    formPanel.add(txtRazon, gbc);

                    gbc.gridx = 0;
                    gbc.gridy = 2;
                    formPanel.add(labelDireccion, gbc);
                    gbc.gridx = 1;
                    formPanel.add(txtDireccion, gbc);

                    gbc.gridx = 0;
                    gbc.gridy = 3;
                    formPanel.add(labelPais, gbc);
                    gbc.gridx = 1;
                    formPanel.add(txtPais, gbc);

                    // Crear un panel para los botones "Guardar" y "Limpiar"
                    JPanel botonesPanel = new JPanel();
                    JButton btnGuardar = new JButton("Guardar");
                    JButton btnLimpiar = new JButton("Limpiar");
                    botonesPanel.add(btnGuardar);
                    botonesPanel.add(btnLimpiar);

                    // Ubicar el panel de botones en el formulario (ocupando dos columnas)
                    gbc.gridx = 0;
                    gbc.gridy = 4;
                    gbc.gridwidth = 2;
                    gbc.anchor = GridBagConstraints.CENTER;
                    formPanel.add(botonesPanel, gbc);

                    // Agregar el formulario al panel de contenido dinámico
                    crudContentPanel.add(formPanel, BorderLayout.CENTER);

                    // Acción para limpiar el formulario: vacía los campos de texto
                    btnLimpiar.addActionListener(ev -> {
                        txtRazon.setText("");
                        txtDireccion.setText("");
                        txtPais.setText("");
                    });

                    // Acción para guardar: aquí se puede agregar la lógica para enviar los datos a la API
                    btnGuardar.addActionListener(ev -> {

                        Empresa empresa = new Empresa();
                        empresa.setRazon_social(txtRazon.getText());
                        empresa.setDireccion_social(txtDireccion.getText());
                        empresa.setPais(txtPais.getText());

                        boolean exito = apiClient.altaEmpresa(empresa);

                        if (exito) {
                            JOptionPane.showMessageDialog(rootPane, "Empresa guardada con exito");
                        } else {
                            JOptionPane.showMessageDialog(rootPane, "Error al guardar la empresa");
                        }
                    });
            // Actualizar el contentPanel para reflejar los cambios
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        btnLeerEmpresa.addActionListener(e -> {
            crudContentPanel.removeAll();

            // 1. Configuración inicial del panel de búsqueda
            JPanel searchFormPanel = new JPanel(new BorderLayout());
            JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JTextField txtId = new JTextField(20);

            // 2. Panel de resultados con capacidad dinámica
            JPanel resultPanel = new JPanel(new BorderLayout());
            resultPanel.setBorder(BorderFactory.createTitledBorder("Resultado de la búsqueda"));

            // 3. Componente inicial en resultPanel
            JLabel initialLabel = new JLabel("El resultado aparecerá aquí", SwingConstants.CENTER);
            resultPanel.add(initialLabel, BorderLayout.CENTER);

            // 4. Configuración del botón MostrarTodo
            JButton btnMostrarTodo = new JButton("Mostrar todo");
            JButton btnBuscar = new JButton("Buscar"); // Botón Buscar declarado aquí

            // ActionListener para el botón Buscar
            btnBuscar.addActionListener(evt -> {
                String idBusqueda = txtId.getText().trim();

                if (idBusqueda.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor ingrese un ID válido");
                    return;
                }

                try {
                    int id = Integer.parseInt(idBusqueda);
                    Empresa empresa = apiClient.obtenerEmpresaPorId(id);

                    resultPanel.removeAll();

                    if (empresa != null) {
                        // Crear panel para mostrar los detalles
                        JPanel detallesPanel = new JPanel(new GridLayout(4, 2, 5, 5));
                        detallesPanel.add(new JLabel("ID Empresa:"));
                        detallesPanel.add(new JLabel(String.valueOf(empresa.getId_empresa())));
                        detallesPanel.add(new JLabel("Razón Social:"));
                        detallesPanel.add(new JLabel(empresa.getRazon_social()));
                        detallesPanel.add(new JLabel("Dirección:"));
                        detallesPanel.add(new JLabel(empresa.getDireccion_social()));
                        detallesPanel.add(new JLabel("País:"));
                        detallesPanel.add(new JLabel(empresa.getPais()));

                        resultPanel.add(detallesPanel, BorderLayout.CENTER);
                    } else {
                        resultPanel.add(new JLabel("Empresa no encontrada", SwingConstants.CENTER), BorderLayout.CENTER);
                    }

                    resultPanel.revalidate();
                    resultPanel.repaint();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "El ID debe ser un número válido");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            });

            // ActionListener para Mostrar Todo (ya existente)
            btnMostrarTodo.addActionListener(ev -> {
                try {
                    List<Empresa> empresas = apiClient.obtenerEmpresas();

                    DefaultTableModel model = new DefaultTableModel(
                            new Object[]{"ID Empresa", "Razón Social", "Dirección", "País"}, 0);

                    for (Empresa emp : empresas) {
                        model.addRow(new Object[]{
                                emp.getId_empresa(),
                                emp.getRazon_social(),
                                emp.getDireccion_social(),
                                emp.getPais()
                        });
                    }

                    JTable table = new JTable(model);
                    JScrollPane scrollPane = new JScrollPane(table);

                    resultPanel.removeAll();
                    resultPanel.add(scrollPane, BorderLayout.CENTER);
                    resultPanel.revalidate();
                    resultPanel.repaint();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al obtener empresas: " + ex.getMessage());
                }
            });

            // 5. Agregar componentes al layout
            inputPanel.add(new JLabel("ID Empresa:"));
            inputPanel.add(txtId);
            inputPanel.add(btnBuscar); // Usamos la variable del botón declarada
            inputPanel.add(btnMostrarTodo);

            searchFormPanel.add(inputPanel, BorderLayout.NORTH);
            searchFormPanel.add(resultPanel, BorderLayout.CENTER);

            crudContentPanel.setLayout(new BorderLayout());
            crudContentPanel.add(searchFormPanel, BorderLayout.CENTER);
            crudContentPanel.revalidate();
            crudContentPanel.repaint();
        });

        btnActualizarEmpresa.addActionListener(e -> {
            crudContentPanel.removeAll();

            // Configuración del formulario
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;

            // Componentes del formulario
            JTextField txtIdEmpresa = new JTextField(20);
            JTextField txtRazonSocial = new JTextField(20);
            JTextField txtDireccionFiscal = new JTextField(20);
            JTextField txtPais = new JTextField(20);

            // Fila 0: ID Empresa
            JLabel lblId = new JLabel("ID Empresa:");
            gbc.gridx = 0;
            gbc.gridy = 0;
            formPanel.add(lblId, gbc);

            JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            idPanel.add(txtIdEmpresa);
            JButton btnBuscar = new JButton("Buscar");
            idPanel.add(btnBuscar);
            gbc.gridx = 1;
            gbc.gridy = 0;
            formPanel.add(idPanel, gbc);

            // Fila 1: Razón Social
            JLabel lblRazonSocial = new JLabel("Razón Social:");
            gbc.gridx = 0;
            gbc.gridy = 1;
            formPanel.add(lblRazonSocial, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            formPanel.add(txtRazonSocial, gbc);

            // Fila 2: Dirección Fiscal
            JLabel lblDireccion = new JLabel("Dirección Fiscal:");
            gbc.gridx = 0;
            gbc.gridy = 2;
            formPanel.add(lblDireccion, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            formPanel.add(txtDireccionFiscal, gbc);

            // Fila 3: País
            JLabel lblPais = new JLabel("País:");
            gbc.gridx = 0;
            gbc.gridy = 3;
            formPanel.add(lblPais, gbc);

            gbc.gridx = 1;
            gbc.gridy = 3;
            formPanel.add(txtPais, gbc);

            // Fila 4: Botones
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JButton btnActualizarEmpresa2 = new JButton("Actualizar");
            JButton btnLimpiar = new JButton("Limpiar");
            buttonsPanel.add(btnActualizarEmpresa2);
            buttonsPanel.add(btnLimpiar);

            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.CENTER;
            formPanel.add(buttonsPanel, gbc);

            // Listeners (mantenemos los mismos que en la versión anterior)
            btnBuscar.addActionListener(ev -> {
                try {
                    int id = Integer.parseInt(txtIdEmpresa.getText().trim());
                    Empresa empresa = apiClient.obtenerEmpresaPorId(id);

                    if (empresa != null) {
                        txtRazonSocial.setText(empresa.getRazon_social());
                        txtDireccionFiscal.setText(empresa.getDireccion_social());
                        txtPais.setText(empresa.getPais());
                    } else {
                        JOptionPane.showMessageDialog(formPanel, "Empresa no encontrada");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(formPanel, "ID debe ser numérico");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(formPanel, "Error: " + ex.getMessage());
                }
            });

            btnActualizarEmpresa2.addActionListener(ev -> {
                try {
                    Empresa empresaActualizada = new Empresa();
                    empresaActualizada.setId_empresa(Integer.parseInt(txtIdEmpresa.getText().trim()));
                    empresaActualizada.setRazon_social(txtRazonSocial.getText().trim());
                    empresaActualizada.setDireccion_social(txtDireccionFiscal.getText().trim());
                    empresaActualizada.setPais(txtPais.getText().trim());

                    boolean exito = apiClient.actualizarEmpresa(
                            empresaActualizada.getId_empresa(),
                            empresaActualizada
                    );

                    if (exito) {
                        JOptionPane.showMessageDialog(formPanel, "Empresa actualizada correctamente");
                    } else {
                        JOptionPane.showMessageDialog(formPanel, "Error al actualizar");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(formPanel, "ID inválido");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(formPanel, "Error: " + ex.getMessage());
                }
            });

            btnLimpiar.addActionListener(ev -> {
                txtIdEmpresa.setText("");
                txtRazonSocial.setText("");
                txtDireccionFiscal.setText("");
                txtPais.setText("");
            });

            crudContentPanel.add(formPanel, BorderLayout.CENTER);
            crudContentPanel.revalidate();
            crudContentPanel.repaint();
        });

        btnEliminarEmpresa.addActionListener(e -> {
            crudContentPanel.removeAll();

            // Panel principal con borde y espaciado
            JPanel deletePanel = new JPanel(new BorderLayout(10, 10));
            deletePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Panel de entrada con GridLayout para mejor alineación
            JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            JLabel lblIdEmpresa = new JLabel("ID Empresa:");
            JTextField txtIdEmpresa = new JTextField();
            JButton btnBorrar = new JButton("Eliminar Empresa");

            // Panel para centrar el botón
            JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonWrapper.add(btnBorrar);

            inputPanel.add(lblIdEmpresa);
            inputPanel.add(txtIdEmpresa);
            inputPanel.add(new JLabel()); // Espacio vacío
            inputPanel.add(buttonWrapper);

            // Panel de resultados con mensaje dinámico
            JPanel resultPanel = new JPanel();
            JLabel lblResultado = new JLabel(" ", SwingConstants.CENTER);
            lblResultado.setForeground(new Color(0, 100, 0)); // Color verde oscuro
            resultPanel.add(lblResultado);

            // Configuración de ActionListener
            btnBorrar.addActionListener(evt -> {
                String idStr = txtIdEmpresa.getText().trim();
                if (idStr.isEmpty()) {
                    JOptionPane.showMessageDialog(deletePanel,
                            "Por favor ingrese un ID válido",
                            "Error",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int id = Integer.parseInt(idStr);

                    // Confirmación antes de eliminar
                    int confirm = JOptionPane.showConfirmDialog(
                            deletePanel,
                            "¿Está seguro que desea eliminar la empresa con ID " + id + "?",
                            "Confirmar eliminación",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean exito = apiClient.borrarEmpresa(id);

                        if (exito) {
                            lblResultado.setText("Empresa eliminada exitosamente");
                            txtIdEmpresa.setText(""); // Limpiar campo

                            // Actualizar mensaje temporalmente
                            new Timer(3000, event -> lblResultado.setText(" ")).start();
                        } else {
                            JOptionPane.showMessageDialog(deletePanel,
                                    "No se pudo eliminar la empresa",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(deletePanel,
                            "El ID debe ser un número válido",
                            "Error de formato",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(deletePanel,
                            "Error: " + ex.getMessage(),
                            "Error del sistema",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            // Ensamblar componentes
            deletePanel.add(inputPanel, BorderLayout.NORTH);
            deletePanel.add(resultPanel, BorderLayout.CENTER);

            crudContentPanel.setLayout(new BorderLayout());
            crudContentPanel.add(deletePanel, BorderLayout.CENTER);
            crudContentPanel.revalidate();
            crudContentPanel.repaint();
        });

        // Actualizar el contentPanel para reflejar los cambios
        contentPanel.revalidate();
        contentPanel.repaint();
    }


    // DESDE AQUI SE MANEJA EL CRUD DE GESTION DE CATEGORIAS
    private void mostrarGestionCategorias() {
        // Limpiar el contenido actual
        contentPanel.removeAll();

        // Panel de submenu para el CRUD de Categorías (disposición horizontal)
        JPanel submenuPanel = new JPanel();
        submenuPanel.setLayout(new BoxLayout(submenuPanel, BoxLayout.X_AXIS));

        // Crear los botones del submenu
        JButton btnCrear = new JButton("Crear");
        JButton btnLeer = new JButton("Leer");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");

        // Establecer dimensiones preferidas y espaciado para los botones
        Dimension buttonDimension = new Dimension(400, 50);
        int espacio = 10;
        btnCrear.setPreferredSize(buttonDimension);
        btnLeer.setPreferredSize(buttonDimension);
        btnActualizar.setPreferredSize(buttonDimension);
        btnEliminar.setPreferredSize(buttonDimension);

        btnCrear.setBorder(BorderFactory.createEmptyBorder(0, espacio, 0, espacio));
        btnLeer.setBorder(BorderFactory.createEmptyBorder(0, espacio, 0, espacio));
        btnActualizar.setBorder(BorderFactory.createEmptyBorder(0, espacio, 0, espacio));
        btnEliminar.setBorder(BorderFactory.createEmptyBorder(0, espacio, 0, espacio));

        // Agregar los botones al submenu
        submenuPanel.add(btnCrear);
        submenuPanel.add(btnLeer);
        submenuPanel.add(btnActualizar);
        submenuPanel.add(btnEliminar);

        // Panel para el contenido dinámico del CRUD
        JPanel crudContentPanel = new JPanel(new BorderLayout());
        crudContentPanel.add(new JLabel("Área de contenido CRUD para Categorías", SwingConstants.CENTER), BorderLayout.CENTER);

        // Configurar contentPanel para que el submenu quede en la parte superior y el contenido dinámico en el centro
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(submenuPanel, BorderLayout.NORTH);
        contentPanel.add(crudContentPanel, BorderLayout.CENTER);

        // ---------- Botón Crear ----------
        btnCrear.addActionListener(e -> {
            crudContentPanel.removeAll();

            // Panel para el formulario de creación
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;

            // Componentes del formulario
            JTextField txtNombre = new JTextField(20);
            JTextField txtDescripcion = new JTextField(20);

            // Configuración de las filas
            // Fila 0: Nombre
            JLabel lblNombre = new JLabel("Nombre:");
            gbc.gridx = 0;
            gbc.gridy = 0;
            formPanel.add(lblNombre, gbc);
            gbc.gridx = 1;
            formPanel.add(txtNombre, gbc);

            // Fila 1: Descripción
            JLabel lblDescripcion = new JLabel("Descripción:");
            gbc.gridx = 0;
            gbc.gridy = 1;
            formPanel.add(lblDescripcion, gbc);
            gbc.gridx = 1;
            formPanel.add(txtDescripcion, gbc);

            // Panel de botones
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            JButton btnCrearCategoria = new JButton("Crear Categoría");
            JButton btnLimpiar = new JButton("Limpiar");
            buttonsPanel.add(btnCrearCategoria);
            buttonsPanel.add(btnLimpiar);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.CENTER;
            formPanel.add(buttonsPanel, gbc);

            // Listeners
            btnLimpiar.addActionListener(evt -> {
                txtNombre.setText("");
                txtDescripcion.setText("");
            });

            btnCrearCategoria.addActionListener(evt -> {

                Categoria cat = new Categoria();
                cat.setNombre(txtNombre.getText());
                cat.setDescripcion(txtDescripcion.getText());

                boolean exito = apiClient.crearCategoria(cat);

                if (exito) {
                    JOptionPane.showMessageDialog(rootPane, "Categoría creada con exito");
                } else {
                    JOptionPane.showMessageDialog(rootPane, "Error al crear categoria");
                }

            });

            crudContentPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
            crudContentPanel.revalidate();
            crudContentPanel.repaint();
        });

        // ---------- Botón Leer ----------
        btnLeer.addActionListener(e -> {
            crudContentPanel.removeAll();

            // Panel contenedor
            JPanel searchFormPanel = new JPanel(new BorderLayout());
            JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JTextField txtId = new JTextField(20);

            // Panel de resultados
            JPanel resultPanel = new JPanel(new BorderLayout());
            resultPanel.setBorder(BorderFactory.createTitledBorder("Resultados"));
            JLabel initialLabel = new JLabel("Los resultados aparecerán aquí", SwingConstants.CENTER);
            resultPanel.add(initialLabel, BorderLayout.CENTER);

            // Botones
            JButton btnMostrarTodo = new JButton("Mostrar todas");
            JButton btnBuscar = new JButton("Buscar por ID");

            // ActionListener para Buscar
            btnBuscar.addActionListener(evt -> {
                String idBusqueda = txtId.getText().trim();
                if (idBusqueda.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ingrese un ID válido");
                    return;
                }

                try {
                    int id = Integer.parseInt(idBusqueda);
                    Categoria cat = apiClient.obtenerCategoriaPorId(id);

                    resultPanel.removeAll();
                    if (cat != null) {
                        JPanel detalles = new JPanel(new GridLayout(3, 2, 5, 5));
                        detalles.add(new JLabel("ID Categoría:"));
                        detalles.add(new JLabel(String.valueOf(cat.getId_categoria())));
                        detalles.add(new JLabel("Nombre:"));
                        detalles.add(new JLabel(cat.getNombre()));
                        detalles.add(new JLabel("Descripción:"));
                        detalles.add(new JLabel(cat.getDescripcion()));
                        resultPanel.add(detalles, BorderLayout.CENTER);
                    } else {
                        resultPanel.add(new JLabel("Categoría no encontrada", SwingConstants.CENTER));
                    }
                    resultPanel.revalidate();
                    resultPanel.repaint();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "ID debe ser numérico");
                }
            });

            // ActionListener para Mostrar Todas
            btnMostrarTodo.addActionListener(ev -> {
                try {
                    List<Categoria> categorias = apiClient.obtenerCategorias();

                    DefaultTableModel model = new DefaultTableModel(
                            new Object[]{"ID", "Nombre", "Descripción"}, 0);

                    for (Categoria cat : categorias) {
                        model.addRow(new Object[]{
                                cat.getId_categoria(),
                                cat.getNombre(),
                                cat.getDescripcion()
                        });
                    }

                    JTable tabla = new JTable(model);
                    JScrollPane scrollPane = new JScrollPane(tabla);

                    resultPanel.removeAll();
                    resultPanel.add(scrollPane, BorderLayout.CENTER);
                    resultPanel.revalidate();
                    resultPanel.repaint();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            });

            // Ensamblar componentes
            inputPanel.add(new JLabel("ID Categoría:"));
            inputPanel.add(txtId);
            inputPanel.add(btnBuscar);
            inputPanel.add(btnMostrarTodo);

            searchFormPanel.add(inputPanel, BorderLayout.NORTH);
            searchFormPanel.add(resultPanel, BorderLayout.CENTER);

            crudContentPanel.add(searchFormPanel, BorderLayout.CENTER);
            crudContentPanel.revalidate();
            crudContentPanel.repaint();
        });

        // ---------- Botón Actualizar ----------
        btnActualizar.addActionListener(e -> {
            crudContentPanel.removeAll();

            // Panel principal
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;

            // Componentes
            JTextField txtIdCategoria = new JTextField(20);
            JTextField txtNombre = new JTextField(20);
            JTextField txtDescripcion = new JTextField(20);

            // Fila 0: ID Categoría
            JLabel lblId = new JLabel("ID Categoría:");
            gbc.gridx = 0;
            gbc.gridy = 0;
            formPanel.add(lblId, gbc);

            JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            idPanel.add(txtIdCategoria);
            JButton btnBuscar = new JButton("Buscar");
            idPanel.add(btnBuscar);
            gbc.gridx = 1;
            gbc.gridy = 0;
            formPanel.add(idPanel, gbc);

            // Fila 1: Nombre
            JLabel lblNombre = new JLabel("Nombre:");
            gbc.gridx = 0;
            gbc.gridy = 1;
            formPanel.add(lblNombre, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            formPanel.add(txtNombre, gbc);

            // Fila 2: Descripción
            JLabel lblDescripcion = new JLabel("Descripción:");
            gbc.gridx = 0;
            gbc.gridy = 2;
            formPanel.add(lblDescripcion, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            formPanel.add(txtDescripcion, gbc);

            // Fila 3: Botones
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            JButton btnActualizarCategoria = new JButton("Actualizar");
            JButton btnLimpiar = new JButton("Limpiar");
            buttonsPanel.add(btnActualizarCategoria);
            buttonsPanel.add(btnLimpiar);

            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.CENTER;
            formPanel.add(buttonsPanel, gbc);

            // Listeners
            btnBuscar.addActionListener(ev -> {
                try {
                    int id = Integer.parseInt(txtIdCategoria.getText().trim());
                    Categoria cat = apiClient.obtenerCategoriaPorId(id);

                    if (cat != null) {
                        txtNombre.setText(cat.getNombre());
                        txtDescripcion.setText(cat.getDescripcion() != null ? cat.getDescripcion() : "");
                    } else {
                        JOptionPane.showMessageDialog(formPanel,
                                "Categoría no encontrada",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(formPanel,
                            "ID debe ser numérico",
                            "Error de formato",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(formPanel,
                            "Error: " + ex.getMessage(),
                            "Error del sistema",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            btnActualizarCategoria.addActionListener(ev -> {
                try {
                    if (txtIdCategoria.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(formPanel,
                                "Debe ingresar un ID válido",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Categoria categoriaActualizada = new Categoria();
                    categoriaActualizada.setNombre(txtNombre.getText().trim());
                    categoriaActualizada.setDescripcion(txtDescripcion.getText().trim());

                    int id = Integer.parseInt(txtIdCategoria.getText().trim());

                    boolean exito = apiClient.actualizarCategoria(id, categoriaActualizada);

                    if (exito) {
                        JOptionPane.showMessageDialog(formPanel,
                                "Categoría actualizada exitosamente",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(formPanel,
                                "Error al actualizar la categoría",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(formPanel,
                            "ID inválido",
                            "Error de formato",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(formPanel,
                            "Error: " + ex.getMessage(),
                            "Error del sistema",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            btnLimpiar.addActionListener(ev -> {
                txtIdCategoria.setText("");
                txtNombre.setText("");
                txtDescripcion.setText("");
            });

            crudContentPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
            crudContentPanel.revalidate();
            crudContentPanel.repaint();
        });

        // ---------- Botón Eliminar ----------
        btnEliminar.addActionListener(e -> {
            crudContentPanel.removeAll();

            // Panel principal con borde y espaciado
            JPanel deletePanel = new JPanel(new BorderLayout(10, 10));
            deletePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Panel de entrada con GridLayout para mejor alineación
            JPanel inputPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            JLabel lblIdCategoria = new JLabel("ID Categoría:");
            JTextField txtIdCategoria = new JTextField();
            txtIdCategoria.setPreferredSize(new Dimension(80, 35));
            JButton btnBorrar = new JButton("Eliminar Categoría");

            // Panel para agrupar componentes
            JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            fieldPanel.add(lblIdCategoria);
            fieldPanel.add(txtIdCategoria);

            inputPanel.add(fieldPanel);
            inputPanel.add(btnBorrar);

            // Panel de resultados
            JPanel resultPanel = new JPanel();
            JLabel lblResultado = new JLabel(" ");
            lblResultado.setForeground(new Color(0, 100, 0)); // Color verde oscuro
            resultPanel.add(lblResultado);

            // Configuración de ActionListener
            btnBorrar.addActionListener(evt -> {
                String idStr = txtIdCategoria.getText().trim();
                if (idStr.isEmpty()) {
                    JOptionPane.showMessageDialog(deletePanel,
                            "Por favor ingrese un ID válido",
                            "Error",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int id = Integer.parseInt(idStr);

                    // Confirmación antes de eliminar
                    int confirm = JOptionPane.showConfirmDialog(
                            deletePanel,
                            "¿Está seguro que desea eliminar la categoría con ID " + id + "?",
                            "Confirmar eliminación",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean exito = apiClient.borrarCategoria(id);

                        if (exito) {
                            lblResultado.setText("¡Categoría eliminada exitosamente!");
                            txtIdCategoria.setText(""); // Limpiar campo

                            // Resetear mensaje después de 3 segundos
                            new Timer(3000, event -> lblResultado.setText(" ")).start();
                        } else {
                            JOptionPane.showMessageDialog(deletePanel,
                                    "No se pudo eliminar la categoría",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(deletePanel,
                            "El ID debe ser un número válido",
                            "Error de formato",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(deletePanel,
                            "Error: " + ex.getMessage(),
                            "Error del sistema",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            // Ensamblar componentes
            deletePanel.add(inputPanel, BorderLayout.NORTH);
            deletePanel.add(resultPanel, BorderLayout.CENTER);

            crudContentPanel.setLayout(new BorderLayout());
            crudContentPanel.add(deletePanel, BorderLayout.CENTER);
            crudContentPanel.revalidate();
            crudContentPanel.repaint();
        });

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // DESDE AQUI SE MANEJA EL CRUD DE GESTION DE USUARIOS
    private void mostrarGestionUsuarios() {
        contentPanel.removeAll();

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel de selección
        JPanel selectionPanel = new JPanel(new GridLayout(0, 1, 10, 10));

        // Combo box para seleccionar usuario
        JLabel lblUsuarios = new JLabel("Seleccionar Usuario:");
        JComboBox<String> cmbUsuarios = new JComboBox<>();

        try {
            List<Usuario> usuarios = apiClient.obtenerUsuarios();
            for (Usuario u : usuarios) {
                cmbUsuarios.addItem(u.getUsername());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(contentPanel,
                    "Error cargando usuarios: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        // Botones
        JButton btnDesactivar = new JButton("Dar de Baja");
        JButton btnActualizarLista = new JButton("Actualizar Lista");

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(btnDesactivar);

        // Ensamblar componentes
        selectionPanel.add(lblUsuarios);
        selectionPanel.add(cmbUsuarios);

        mainPanel.add(selectionPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Listeners
        btnDesactivar.addActionListener(e -> {
            String username = (String) cmbUsuarios.getSelectedItem();

            if (username == null || username.isEmpty()) {
                JOptionPane.showMessageDialog(contentPanel,
                        "Seleccione un usuario de la lista",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(contentPanel,
                    "¿Está seguro de desactivar al usuario: " + username + "?",
                    "Confirmar desactivación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    boolean exito = apiClient.desactivarUsuario(username);

                    if (exito) {
                        JOptionPane.showMessageDialog(contentPanel,
                                "Usuario desactivado exitosamente",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                        btnActualizarLista.doClick(); // Actualizar lista
                    } else {
                        JOptionPane.showMessageDialog(contentPanel,
                                "Error al desactivar el usuario",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(contentPanel,
                            "Error: " + ex.getMessage(),
                            "Error del sistema",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        contentPanel.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // DESDE AQUI SE MANEJA EL CRUD DE GESTION DE ADMINISTRADORES
    private void mostrarGestionAdministradores(){
        // Limpiar el panel de contenido principal
        contentPanel.removeAll();

        // Crear el panel de submenu para el CRUD (disposición horizontal)
        JPanel submenuPanel = new JPanel();
        submenuPanel.setLayout(new BoxLayout(submenuPanel, BoxLayout.X_AXIS));

        // Crear los botones para las operaciones CRUD
        JButton btnCrear = new JButton("Crear");
        JButton btnLeer = new JButton("Leer");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");

        // Establecer dimensiones preferidas y espaciado para los botones
        Dimension buttonDimension = new Dimension(400, 50);
        int espacio = 10;
        btnCrear.setPreferredSize(buttonDimension);
        btnLeer.setPreferredSize(buttonDimension);
        btnActualizar.setPreferredSize(buttonDimension);
        btnEliminar.setPreferredSize(buttonDimension);

        btnCrear.setBorder(BorderFactory.createEmptyBorder(0, espacio, 0, espacio));
        btnLeer.setBorder(BorderFactory.createEmptyBorder(0, espacio, 0, espacio));
        btnActualizar.setBorder(BorderFactory.createEmptyBorder(0, espacio, 0, espacio));
        btnEliminar.setBorder(BorderFactory.createEmptyBorder(0, espacio, 0, espacio));

        // Agregar los botones al panel de submenu
        submenuPanel.add(btnCrear);
        submenuPanel.add(btnLeer);
        submenuPanel.add(btnActualizar);
        submenuPanel.add(btnEliminar);

        // Crear un panel para el contenido dinámico del CRUD
        JPanel crudContentPanel = new JPanel(new BorderLayout());
        crudContentPanel.add(new JLabel("Área de contenido CRUD Administradores", SwingConstants.CENTER), BorderLayout.CENTER);

        // Configurar el contentPanel principal con BorderLayout:
        // - Norte: submenu con botones CRUD.
        // - Centro: panel dinámico con el contenido según la acción seleccionada.
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(submenuPanel, BorderLayout.NORTH);
        contentPanel.add(crudContentPanel, BorderLayout.CENTER);

        // Acción para el botón "Crear": muestra un formulario para crear un administrador
        btnCrear.addActionListener(e -> {
            crudContentPanel.removeAll();

            // Panel principal
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            // Panel del formulario
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;

            // Campo Username
            JLabel lblUsername = new JLabel("Username:");
            JTextField txtUsername = new JTextField(25);
            gbc.gridx = 0;
            gbc.gridy = 0;
            formPanel.add(lblUsername, gbc);
            gbc.gridx = 1;
            formPanel.add(txtUsername, gbc);

            // Campo Nombre
            JLabel lblNombre = new JLabel("Nombre:");
            JTextField txtNombre = new JTextField(25);
            gbc.gridx = 0;
            gbc.gridy = 1;
            formPanel.add(lblNombre, gbc);
            gbc.gridx = 1;
            formPanel.add(txtNombre, gbc);

            // Campo Apellido
            JLabel lblApellido = new JLabel("Apellidos:");
            JTextField txtApellido = new JTextField(25);
            gbc.gridx = 0;
            gbc.gridy = 2;
            formPanel.add(lblApellido, gbc);
            gbc.gridx = 1;
            formPanel.add(txtApellido, gbc);

            // Campo Email
            JLabel lblEmail = new JLabel("Email:");
            JTextField txtEmail = new JTextField(25);
            gbc.gridx = 0;
            gbc.gridy = 3;
            formPanel.add(lblEmail, gbc);
            gbc.gridx = 1;
            formPanel.add(txtEmail, gbc);

            // Campo Password
            JLabel lblPassword = new JLabel("Password:");
            JPasswordField txtPassword = new JPasswordField(25);
            gbc.gridx = 0;
            gbc.gridy = 4;
            formPanel.add(lblPassword, gbc);
            gbc.gridx = 1;
            formPanel.add(txtPassword, gbc);

            //Campo id_perfil
            JLabel lblPerfil = new JLabel("id_perfil");
            JTextField txtPerfil = new JTextField(25);
            gbc.gridx=0;
            gbc.gridy =5;
            formPanel.add(lblPerfil,gbc);
            gbc.gridx=1;
            formPanel.add(txtPerfil,gbc);

            // Panel de botones
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            JButton btnGuardar = new JButton("Crear Usuario");
            JButton btnLimpiar = new JButton("Limpiar");
            buttonPanel.add(btnGuardar);
            buttonPanel.add(btnLimpiar);

            // Posicionar botones
            gbc.gridx = 0;
            gbc.gridy = 6;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            formPanel.add(buttonPanel, gbc);

            // Panel de resultado
            JPanel resultPanel = new JPanel(new BorderLayout(10, 10));
            JLabel lblUsuarioCreado = new JLabel(" ");
            JButton btnAsignarAdmin = new JButton("Asignar como Admin");
            resultPanel.add(lblUsuarioCreado, BorderLayout.CENTER);
            resultPanel.add(btnAsignarAdmin, BorderLayout.EAST);
            resultPanel.setBorder(BorderFactory.createTitledBorder("Acciones posteriores"));

            // Configurar acciones
            btnGuardar.addActionListener(ev -> {
                try {
                    // Validación de campos
                    if (txtUsername.getText().isEmpty() ||
                            txtNombre.getText().isEmpty() ||
                            txtApellido.getText().isEmpty() ||
                            txtEmail.getText().isEmpty() ||
                            txtPassword.getPassword().length == 0 ||
                            txtPerfil.getText().isEmpty()) {

                        JOptionPane.showMessageDialog(crudContentPanel,
                                "Todos los campos son obligatorios",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (!txtEmail.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                        JOptionPane.showMessageDialog(crudContentPanel,
                                "Formato de email inválido",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Crear objeto Usuario
                    Usuario nuevoUsuario = new Usuario();
                    nuevoUsuario.setUsername(txtUsername.getText().trim());
                    nuevoUsuario.setNombre(txtNombre.getText().trim());
                    nuevoUsuario.setApellidos(txtApellido.getText().trim());
                    nuevoUsuario.setEmail(txtEmail.getText().trim());
                    nuevoUsuario.setPassword(new String(txtPassword.getPassword()));

                    int idPerfil = Integer.parseInt(txtPerfil.getText().trim());

                    // Llamar al API
                    boolean exito = apiClient.crearUsuario(idPerfil,nuevoUsuario);

                    if (exito) {
                        lblUsuarioCreado.setText("Usuario creado: " + nuevoUsuario.getUsername());
                        JOptionPane.showMessageDialog(crudContentPanel,
                                "Usuario creado exitosamente!",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(crudContentPanel,
                                "Error al crear el usuario",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(crudContentPanel,
                            "Error: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            btnAsignarAdmin.addActionListener(ev -> {
                String username = lblUsuarioCreado.getText().replace("Usuario creado: ", "");
                if (!username.isEmpty()) {
                    try {
                        boolean exito = apiClient.asignarAdmin(username);
                        if (exito) {
                            JOptionPane.showMessageDialog(crudContentPanel,
                                    "Usuario asignado como administrador",
                                    "Éxito",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(crudContentPanel,
                                    "Error al asignar privilegios",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(crudContentPanel,
                                "Error: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            btnLimpiar.addActionListener(ev -> {
                txtUsername.setText("");
                txtNombre.setText("");
                txtApellido.setText("");
                txtEmail.setText("");
                txtPassword.setText("");
                lblUsuarioCreado.setText(" ");
            });

            // Ensamblar componentes
            mainPanel.add(formPanel, BorderLayout.NORTH);
            mainPanel.add(resultPanel, BorderLayout.SOUTH);

            crudContentPanel.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
            crudContentPanel.revalidate();
            crudContentPanel.repaint();
        });

        // Acción para el botón "Leer": muestra un listado de administradores (placeholder)
        btnLeer.addActionListener(e -> {
            crudContentPanel.removeAll();
            crudContentPanel.add(new JLabel("Listado de administradores", SwingConstants.CENTER), BorderLayout.CENTER);
            crudContentPanel.revalidate();
            crudContentPanel.repaint();
        });

        // Acción para el botón "Actualizar": muestra un formulario para actualizar un administrador (placeholder)
        btnActualizar.addActionListener(e -> {
            crudContentPanel.removeAll();
            crudContentPanel.add(new JLabel("Formulario para actualizar administrador", SwingConstants.CENTER), BorderLayout.CENTER);
            crudContentPanel.revalidate();
            crudContentPanel.repaint();
        });

        // Acción para el botón "Eliminar": muestra un formulario para confirmar eliminación (placeholder)
        btnEliminar.addActionListener(e -> {
            crudContentPanel.removeAll();
            crudContentPanel.add(new JLabel("Confirmar eliminación de administrador", SwingConstants.CENTER), BorderLayout.CENTER);
            crudContentPanel.revalidate();
            crudContentPanel.repaint();
        });

        // Actualizar el contentPanel principal para reflejar los cambios
        contentPanel.revalidate();
        contentPanel.repaint();
    }


    }
