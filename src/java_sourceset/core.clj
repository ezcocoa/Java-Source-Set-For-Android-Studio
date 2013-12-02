;;
;; this is project that just follow the python version that someone makes.
;; created by hojun baek.
;;
(ns java-sourceset.core
  (:gen-class :main true)
  (:import java.io.File))

(def src-folder "<sourceFolder url=\"file://$MODULE_DIR$/src/test/java\" isTestSource=\"true\" />\n")
(def content-tag "<content url=\"file://$MODULE_DIR$\">\n")
(def last-updated (ref 0))
(defn start-monitoring [file-name]
  (future
  (loop []
     (Thread/sleep 10000)
        (let [updated (. (java.io.File. file-name) lastModified)]
          (if (not= last-updated updated)
            (let [f (slurp file-name)]
             (if-not (.contains f src-folder)
                (let [ff (clojure.string/replace f content-tag (str content-tag src-folder))]
                 (with-open [w (clojure.java.io/writer file-name :append false)]
                   (.write w ff))
                  (dosync (ref-set last-updated updated))
                 (println (str "[" (java.util.Date.) "] " "Updated file.")))))))
        (recur))))

(defn -main
  "Command application"
  [& args]
  (if args
      (if (. (java.io.File. (first args)) exists )
        (do
          (println "Start monitoring...")
          (println "If you want to exit this program, press [ctl+c].")
          (start-monitoring (first args)))
        (println (str "The '" (first args) "' file doesn't exist.")))
    (println "Usage: java -jar java-sourceset <project.iml>")))
