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

        private List<Aluno> studentId;


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
    }
