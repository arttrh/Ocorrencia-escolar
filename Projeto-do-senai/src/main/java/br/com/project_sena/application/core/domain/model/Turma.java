    package br.com.project_sena.application.core.domain.model;

    import br.com.project_sena.application.core.domain.enums.TurmaEnum;
    import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;

    import java.time.LocalDateTime;
    import java.util.List;

    public class Turma {

        private Long id;
        private String className;
        private LocalDateTime classYear;
        private LocalDateTime semestry;
        private Integer turmaCheia = 36;

        //Enums
        private TurmaEnum turmaEnum;
        private TurmaTurnoEnum turmaTurnoEnum;

        private List<Aluno> aluno;

        public Turma() {
        }

        public Turma(Long id, String className, TurmaTurnoEnum turnoTurma, LocalDateTime classYear, TurmaEnum turmaEnum, LocalDateTime semestry) {
            this.id = id;
            this.className = className;
            this.turmaTurnoEnum = turnoTurma;
            this.classYear = classYear;
            this.turmaEnum = TurmaEnum.ATIVA;
            this.semestry = semestry;
        }

        public Turma(String className, TurmaTurnoEnum turnoTurma, LocalDateTime classYear, LocalDateTime semestry) {
            this.className = className;
            this.classYear = classYear;
            this.turmaTurnoEnum = turnoTurma;
            this.semestry = semestry;
        }

        public Turma(String className, TurmaTurnoEnum turnoTurma, LocalDateTime classYear, TurmaEnum turmaEnum, LocalDateTime semestry) {
            this.className = className;
            this.classYear = classYear;
            this.turmaEnum = turmaEnum;
            this.turmaTurnoEnum = turnoTurma;
            this.semestry = semestry;
        }

        public Long getId() {
            return id;
        }

        public String getClassName() {
            return className;
        }

        public LocalDateTime getClassYear() {
            return classYear;
        }

        public TurmaEnum getTurmaEnum() {
            return turmaEnum;
        }

        public TurmaTurnoEnum getTurmaTurnoEnum() {
            return turmaTurnoEnum;
        }

        public LocalDateTime getSemestry() {
            return semestry;
        }

        public List<Aluno> getAluno() {
            return aluno;
        }

        public void setAluno(List<Aluno> aluno) {
            this.aluno = aluno;
        }

        public Integer getTurmaCheia() {
            return turmaCheia;
        }

        public void atualizarTurma(String className,
                                   TurmaTurnoEnum turmaTurnoEnum,
                                   LocalDateTime classYear,
                                   TurmaEnum turmaEnum){
            if (className != null && !className.isBlank()){
                this.className = className;
            }
           if (turmaTurnoEnum != null){
               this.turmaTurnoEnum = turmaTurnoEnum;
           }
            if (classYear != null){
                this.classYear = classYear;
            }
            if (turmaEnum != null){
                this.turmaEnum = turmaEnum;
            }
        }

        public void excluir(){
            this.turmaEnum = TurmaEnum.CANCELADA;
        }

        public void reativar(){
            this.turmaEnum = TurmaEnum.ATIVA;
        }
    }
