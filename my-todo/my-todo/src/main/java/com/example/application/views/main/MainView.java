package com.example.application.views.main;

//import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

import com.example.application.LivroRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Div; // Importe Div para criar um contêiner

import org.springframework.stereotype.Repository;

//@Route("livros")

@Route("")

public class MainView extends VerticalLayout {

    private HorizontalLayout nomeAutorLayout = new HorizontalLayout();
    private HorizontalLayout dataAvaliacaoLayout = new HorizontalLayout(); // Novo layout para data e avaliação
    
    private TextField nomeLivro = new TextField("Nome do Livro");
    private TextField autorLivro = new TextField("Autor do Livro");
    private Checkbox terminouLeitura = new Checkbox("Já terminou de ler o livro?");
    private DatePicker dataConclusao = new DatePicker("Data de Conclusão de Leitura");
    private TextField avaliacao = new TextField("Avaliação (de 1 a 5)");
    private Button adicionarLivro = new Button("Adicionar Livro", new Icon(VaadinIcon.PLUS));
    private Button editarLivro = new Button("Salvar Edição", new Icon(VaadinIcon.CHECK));
    private Button excluirLivro = new Button("Excluir Livro", new Icon(VaadinIcon.TRASH));
    private HorizontalLayout botoesLayout = new HorizontalLayout(); // Novo layout para os botões

    private Grid<Livro> grid = new Grid<>(Livro.class);
    private Livro livroEmEdicao = null;

    private LivroRepository livroRepository;


    public MainView(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
        // Configurações iniciais
        setSpacing(true);
        setClassName("main-view");

        // Configura o botão "Adicionar Livro" com o ícone e estilo azul claro
        adicionarLivro.addClickListener(event -> adicionarLivro());
        adicionarLivro.getStyle().set("background-color", "lightblue");
        adicionarLivro.getStyle().set("color", "black");
        adicionarLivro.setWidth("140px");
        adicionarLivro.getStyle().set("font-size", "12px");

        editarLivro.getStyle().set("font-size", "10px");
        excluirLivro.getStyle().set("font-size", "10px");
        editarLivro.setWidth("120px");
        excluirLivro.setWidth("120px");

        // Crie um contêiner <div> para envolver o título
        Div tituloContainer = new Div();

        // Adiciona o título "Adicione seus livros!" usando o elemento H1 dentro do contêiner
        H1 titulo = new H1("Lista de leitura - adicione seus livros!");
        tituloContainer.add(titulo);

        add(tituloContainer);

        add(new Paragraph(
                "-> Após ter adicionado o livro, caso queira editar as informações dele, selecione-o na lista, faça a edição e depois clique em \"Salvar Edição\". Caso queira excluí-lo, selecione-o na lista e clique em \"Excluir Livro\""));

        // Configura o evento de alteração da checkbox "terminouLeitura"
        terminouLeitura.addValueChangeListener(event -> toggleCamposConclusao());

        // Inicialmente, oculta os campos de data de conclusão e avaliação
        dataConclusao.setVisible(false);
        avaliacao.setVisible(false);

         // Configura os botões de edição e exclusão
        editarLivro.setEnabled(false);
        editarLivro.addClickListener(event -> salvarEdicao());
        excluirLivro.setEnabled(false);
        excluirLivro.addClickListener(event -> excluirLivro());

        // Configura o layout dos botões
        botoesLayout.add(editarLivro, excluirLivro);
        botoesLayout.setSpacing(true); // Adiciona um espaçamento entre os botões

        // Configura a grade
        grid.setColumns("nome", "autor", "terminouLeitura", "dataConclusao", "avaliacao");
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.asSingleSelect().addValueChangeListener(event -> editarLivro(event.getValue()));
        //grid.setItems(livros);
        grid.setItems(livroRepository.findAll());

        // Define a cor de fundo azul claro e adiciona grades à grade
        grid.getStyle().set("border", "1px solid #ccc");

        // Adiciona "Nome do Livro" e "Autor do Livro" em um HorizontalLayout
        nomeAutorLayout.add(nomeLivro, autorLivro);

        // Adiciona "Nome do Livro" e "Autor do Livro" em um HorizontalLayout
        nomeAutorLayout.add(nomeLivro, autorLivro);
        
        // Adiciona componentes ao layout
        dataAvaliacaoLayout.add(dataConclusao, avaliacao); // Adiciona os campos de data e avaliação ao novo layout
        add(nomeAutorLayout, terminouLeitura, dataAvaliacaoLayout, adicionarLivro, botoesLayout, grid);
    }    

    private void toggleCamposConclusao() {
        dataConclusao.setVisible(terminouLeitura.getValue());
        avaliacao.setVisible(terminouLeitura.getValue());
    }

    private void adicionarLivro() {
        Livro livro = new Livro();

        livro.setNome(nomeLivro.getValue());
        livro.setAutor(autorLivro.getValue());
        livro.setTerminouLeitura(terminouLeitura.getValue());
        livro.setDataConclusao(dataConclusao.getValue());
        livro.setAvaliacao(avaliacao.getValue());



        livroRepository.save(livro);
        grid.setItems(livroRepository.findAll());

        // Limpa os campos após adicionar o livro
        limparCampos();
    }

    private void editarLivro(Livro livro) {
        if (livro != null) {
            livroEmEdicao = livro;
            nomeLivro.setValue(livro.getNome());
            autorLivro.setValue(livro.getAutor());
            terminouLeitura.setValue(livro.isTerminouLeitura());
            dataConclusao.setValue(livro.getDataConclusao());
            avaliacao.setValue(livro.getAvaliacao());

            editarLivro.setEnabled(true);
            excluirLivro.setEnabled(true);

        } else {
            limparCampos();
        }
    }

    private void salvarEdicao() {
        if (livroEmEdicao != null) {
            livroEmEdicao.setNome(nomeLivro.getValue());
            livroEmEdicao.setAutor(autorLivro.getValue());
            livroEmEdicao.setTerminouLeitura(terminouLeitura.getValue());
            livroEmEdicao.setDataConclusao(dataConclusao.getValue());
            livroEmEdicao.setAvaliacao(avaliacao.getValue());
            
            livroRepository.save(livroEmEdicao);
            grid.getDataProvider().refreshItem(livroEmEdicao);

            limparCampos();
        }
    }

    private void excluirLivro() {
        if (livroEmEdicao != null) {
            livroRepository.delete(livroEmEdicao);
            grid.setItems(livroRepository.findAll());

            limparCampos();
        }
    }

    private void limparCampos() {
        livroEmEdicao = null;
        nomeLivro.clear();
        autorLivro.clear();
        terminouLeitura.clear();
        dataConclusao.clear();
        avaliacao.clear();
        editarLivro.setEnabled(false);
        excluirLivro.setEnabled(false);
    }


}

