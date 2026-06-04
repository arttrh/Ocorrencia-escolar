    package br.com.project_sena.application.core.domain.model;

    import br.com.project_sena.application.core.domain.enums.TurmaEnum;

    import java.util.List;

    public class Turma {

        private Long id;
        private String className;
        private String shift;
        private Integer classYear;

        //Enums
        private TurmaEnum turmaEnum;

        public Turma() {
        }

        public Turma(Long id, String className, String shift, Integer classYear, TurmaEnum turmaEnum) {
            this.id = id;
            this.className = className;
            this.shift = shift;
            this.classYear = classYear;
            this.turmaEnum = turmaEnum;
        }

        public Turma(String className, String shift, Integer classYear) {
            this.className = className;
            this.shift = shift;
            this.classYear = classYear;
        }

        public Turma(String className, String shift, Integer classYear, TurmaEnum turmaEnum) {
            this.className = className;
            this.shift = shift;
            this.classYear = classYear;
            this.turmaEnum = turmaEnum;
        }

        public Long getId() {
            return id;
        }

        public String getClassName() {
            return className;
        }

        public String getShift() {
            return shift;
        }

        public Integer getClassYear() {
            return classYear;
        }

        public TurmaEnum getTurmaEnum() {
            return turmaEnum;
        }

        public void atualizarTurma(String className,
                                   String shift,
                                   Integer classYear,
                                   TurmaEnum turmaEnum){
            if (className != null && !className.isBlank()){
                this.className = className;
            }
            if (shift != null && !shift.isBlank()){
                this.shift = shift;
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
