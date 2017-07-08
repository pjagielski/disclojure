(defproject pjagielski/disclojure "0.1.5-SNAPSHOT"
  :description "A live coding environment for Overtone and Leipzig"
  :url "https://github.com/pjagielski/disclojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [overtone "0.10.0"]
                 [leipzig "0.11.0-SNAPSHOT"]
                 [prismatic/plumbing "0.5.3"]
                 [org.clojure/java.data "0.1.1"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [proto-repl "0.3.1"]]

  :deploy-repositories  [["releases" :clojars]]
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["deploy" "clojars"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]])
