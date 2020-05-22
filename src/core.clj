(ns core
  (:require [cljfmt-runner.check :as fmt-check]
            [cljfmt-runner.fix :as fmt-fix]
            [clojure.string :as string]
            [clojure.tools.cli :as cli])
  (:gen-class))

(defn- abort [& msg]
  (binding [*out* *err*]
    (when (seq msg)
      (apply println msg))
    (System/exit 1)))

(def ^:private cli-options
  [["-c" "--check" "Check for any formatting errors"]
   ["-f" "--fix"   "Fix any formatting errors"]
   ["-h" "--help"  "Print this help message and exit"]])

(defn- parse-opts
  [args]
  (let [parsed-opts (cli/parse-opts args cli-options)]
    (if (:errors parsed-opts)
      (abort (:errors parsed-opts))
      parsed-opts)))

(defn -main
  [& args]
  (let [parsed-opts (parse-opts args)
        options (:options parsed-opts)
        arguments (string/join (:arguments parsed-opts))]
    (if (:help options)
      (do (println "cljfmt-runner [OPTIONS]")
          (println (:summary parsed-opts)))
      (cond
        (:check options) (fmt-check/-main arguments)
        (:fix options) (fmt-fix/-main arguments)))))
