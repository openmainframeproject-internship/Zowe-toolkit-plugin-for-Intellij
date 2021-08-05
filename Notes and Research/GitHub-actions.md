Useful links:
   
  - documentation: https://docs.github.com/en/actions

  - where I put my workflows: https://github.com/MaliMi97/For-Mainframe/tree/GHactions/.github/workflows   (note: the version 0.2 of the plugin is still veing sued in this branch)

Simple workflow:


  - This will be a summary of what I read from the documentation and tried to implement. It should be noted that because of testing reasons, some workflows might have a workflow_dispatch option, which allows the user to trigger the action manually whenever they want. This option works only if the file is in default branch.

  - The simplest workflow, which can be used from our purposes can be pretty much copied from the documentation https://docs.github.com/en/actions/guides/building-and-testing-java-with-gradle. My equivalent is here https://github.com/MaliMi97/For-Mainframe/blob/GHactions/.github/workflows/oneJob.yml. There are two thing to note:

    - The command **./gradlew build** automatically runs unit tests after build.
      - if you want to run unit tests separately, you should use **./gradlew build -x test**  the **-x test** means that the test task will be excluded.
  
    - According to the documentation https://docs.github.com/en/actions/guides/building-and-testing-java-with-gradle, some cache files need to be removed before the cache upload actually happens in order for teh caching to run smoothly. 


Trying to run test in parallel:

  - The idea is that as more and more api and especially ui tests will be implemented, it might be favourable to run the tests in parallel. The GitHub actions automatically, unless said otherwise, runs the jobs in workflow in parallel, which means that all we need to do is to make independ jobs. But this means that there will be a necessity to build gradle in each job, which will slow down the workflow. So for a small amount of tests, running the test in parallel might not only be unnecessary, but will also be detrimental. Another complication migth arise with caching. We do not know for sure, when will a new runner be allocated. So there might be an issue when if one job downloads cache and other job uploads cache with the same key at the same time. Here are some ideas, which I have been toying with and tried to implement:
  - The first thing I tried is to have one job, which builds and caches gradle and then two test jobs that depend on the build job and run tests https://github.com/MaliMi97/For-Mainframe/blob/GHactions/.github/workflows/moreJobs.yml. In order to get rid of the potential caching problem, I copied the caching action into a custom repository and got rid of the upload. So the test jobs only download cache. https://github.com/MaliMi97/cache
      - The demerit is that one would need to constantly manually update the https://github.com/MaliMi97/cache repository, which would be annoying.
      - Plus you have to build gradle several times, which takes time.
      - The better thing to do would be to just go and have two test jobs that build gradle and cache it under different key.

   - The second thing is the same as first, but instead of downloading cache, you download an artifact. The build job uploads the build and gradle directories as an artifact and the test jobs use them. The gradle will still need to run some tasks before running the test, but it will be a bit quicker. The problem is that 
      - A.) You still need to build gradle several times and uploading the artifact takes time.
      - B.) If you upload and download the artifact in the same workflow, there can be spikes in the downloading speed. For example an up to 70 MB artifact had downloading range of between 30 seconds and 6 minutes.
      - In other words, this is even worse than the first option.

   - The third thing is same as the second, except that you make a separate workflow which uploads the artifact each day. every day the a workflow will upload an artifact. Whenever you want to test the code, the testing workflow will download the artifact, use it to build gradle and do teh tests.
      - the uploading workflow: https://github.com/MaliMi97/For-Mainframe/blob/GHactions/.github/workflows/uploadGradle.yml
      - the testing workflow: https://github.com/MaliMi97/For-Mainframe/blob/GHactions/.github/workflows/dwnAndTest.yml
      - The good thing might be that the gradle version will be the same in every workflow.
      - And the same thing is bad, because it would bring another complication when editing the build.gradle file.


   - All in all, it seems the best possible approach is to have several testing jobs or workflow, each of which wuild build gradle and cache it under different key https://github.com/MaliMi97/For-Mainframe/blob/GHactions/.github/workflows/moreJobsFinal.yml.
      - the one demerit of this approach is that at the time of writing this, you need to repeat code. But once some features of composite actions are implemented, it should be possible to make an action, which does the whole set-up including downloading repo, setting up a JDK and building gradle.
