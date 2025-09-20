//package za.co.sp.command;
//
//import lombok.extern.slf4j.Slf4j;
//import org.jline.terminal.Terminal;
//import org.springframework.shell.command.annotation.Command;
//import org.springframework.shell.table.BorderStyle;
//import org.springframework.shell.table.TableBuilder;
//import org.springframework.shell.table.TableModelBuilder;
//import za.co.sp.entity.Job;
//import za.co.sp.service.JobService;
//
//@Slf4j
//@Command
//public record JobCommand(JobService jobService, Terminal terminal) {
//
//    @Command(command = "list-jobs", alias = "ll", description = "List all available/scrapped jobs in the database")
//    public void listJobs() {
//        TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
//        modelBuilder.addRow().addValue("Title").addValue("Location").addValue("Salary").addValue("Type");
//
//        for (Job job : jobService.getAll()) {
//            modelBuilder.addRow().addValue(job.getTitle()).addValue(job.getArea()).addValue(job.getSalary()).addValue(job.getJobType());
//        }
//
//        TableBuilder tableBuilder = new TableBuilder(modelBuilder.build());
//        tableBuilder.addFullBorder(BorderStyle.fancy_light);
//        String table = tableBuilder.build().render(terminal.getWidth());
//        System.out.println(table);
//
//    }
//
//    @Command(command = "scrape", description = "Web scrape career junction for given job title")
//    public void scrapeJob(String title) {
//        jobService.scrape(title);
//    }
//
//    @Command(command = "size", description = "Print the total number of jobs in the database")
//    public void size() {
//        System.out.println("Total number of jobs: " + jobService.getAll().size());
//    }
//}
