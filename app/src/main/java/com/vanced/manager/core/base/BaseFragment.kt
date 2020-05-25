package com.vanced.manager.core.base

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.vanced.manager.core.installer.SplitInstallActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import zlc.season.rxdownload4.delete
import zlc.season.rxdownload4.download
import zlc.season.rxdownload4.file
import zlc.season.rxdownload4.task.Task
import zlc.season.rxdownload4.utils.getFileNameFromUrl

open class BaseFragment : Fragment() {

    private var disposable: Disposable? = null

    fun openUrl(Url: String, color: Int) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(requireContext(), color))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(Url))
    }

    fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun downloadSplit(apk: String, apkVar: String, isInstalling: Boolean, loadBar: ProgressBar, navigate: Int) {
        val baseurl = "https://x1nto.github.io/VancedFiles/Splits/"
        val url: String =
            when(apk) {
                "theme" -> baseurl + "Theme/" + apkVar + ".apk"
                "lang" -> baseurl + "Language/split_config." + apkVar + ".apk"
                "arch" -> baseurl + "Config/config." + apkVar + ".apk"
                else -> return
            }

        val task = activity?.cacheDir?.path?.let {
            Task(
                url = url,
                saveName = getFileNameFromUrl(url),
                savePath = it
            )
        }

        if (task?.file()?.exists()!!)
            task.delete()

        disposable = task.download()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { progress ->
                    loadBar.visibility = View.VISIBLE
                    loadBar.progress = progress.percent().toInt()
                },
                onComplete = {
                    loadBar.visibility = View.GONE
                    if (isInstalling) {
                        if (apk == "lang") {
                            if (apkVar != "en")
                                downloadEn(loadBar)
                        }
                        else launchInstaller()

                    } else {
                        view?.findNavController()?.navigate(navigate)
                    }

                },
                onError = { throwable ->
                    Toast.makeText(requireContext(), throwable.toString(), Toast.LENGTH_SHORT).show()
                }
            )
    }

    private fun downloadEn(loadBar: ProgressBar) {
        val url = "https://x1nto.github.io/VancedFiles/Splits/Language/split_config.en.apk"

        val task = activity?.cacheDir?.path?.let {
            Task(
                url = url,
                saveName = getFileNameFromUrl(url),
                savePath = it
            )
        }
        if (task?.file()?.exists()!!)
            task.delete()

        disposable = task
            .download()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {progress ->
                    loadBar.visibility = View.VISIBLE
                    loadBar.progress = progress.percent().toInt()
                },
                onComplete = {
                    loadBar.visibility = View.GONE
                    launchInstaller()
                },
                onError = { throwable ->
                    Toast.makeText(requireContext(), throwable.toString(), Toast.LENGTH_SHORT).show()
                }

            )

    }

    private fun launchInstaller() {
        val intent = Intent(requireContext(), SplitInstallActivity::class.java)
        activity?.startActivityForResult(intent, 666)
    }


}