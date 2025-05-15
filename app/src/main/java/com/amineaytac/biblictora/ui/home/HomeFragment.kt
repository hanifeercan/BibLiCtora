package com.amineaytac.biblictora.ui.home

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.akexorcist.snaptimepicker.SnapTimePickerDialog
import com.akexorcist.snaptimepicker.TimeValue
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.databinding.FragmentHomeBinding
import com.amineaytac.biblictora.ui.discover.DiscoverFragment
import com.amineaytac.biblictora.ui.home.notification.AlarmReceiver
import com.amineaytac.biblictora.ui.home.notification.PreferenceHelper
import com.amineaytac.biblictora.ui.readinglist.ReadingListFragment
import com.amineaytac.biblictora.util.viewBinding
import com.yagmurerdogan.toasticlib.Toastic
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val discoverFragment = DiscoverFragment()
    private val readingListFragment = ReadingListFragment()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindTabLayout()
        bindSwitch()
    }

    private fun bindTabLayout() {
        val fragmentList = arrayListOf(
            discoverFragment, readingListFragment
        )

        val adapter = ViewPagerAdapter(
            fragmentList, childFragmentManager, lifecycle
        )

        val tabLayoutMediator = adapter.createTabLayoutMediator(binding)
        binding.viewPager.adapter = adapter
        tabLayoutMediator.attach()
    }

    private fun bindSwitch() = with(binding) {

        val preferenceHelper = PreferenceHelper(requireContext())
        val hour = preferenceHelper.getHour()
        val minute = preferenceHelper.getMinute()

        if (hour != -1 || minute != -1) {
            switchNotification.isChecked = true
            switchNotification.thumbTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(), R.color.moselle_green
                )
            )
        }

        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchNotification.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(), R.color.moselle_green
                    )
                )
                bindTimePicker()
            } else {
                switchNotification.thumbTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                preferenceHelper.setHourAndMinute(-1, -1)
                Toastic.toastic(
                    context = requireContext(),
                    message = getString(R.string.no_notification),
                    duration = Toastic.LENGTH_SHORT,
                    type = Toastic.WARNING,
                    isIconAnimated = true
                ).show()
            }
        }
    }

    private fun bindTimePicker() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001
                )
            }
        }

        val preferenceHelper = PreferenceHelper(requireContext())
        var hour = preferenceHelper.getHour()
        var minute = preferenceHelper.getMinute()

        if (hour == -1 || minute == -1) {
            val c = Calendar.getInstance()
            hour = c.get(Calendar.HOUR_OF_DAY)
            minute = c.get(Calendar.MINUTE)
        }

        SnapTimePickerDialog.Builder().apply {
            setTitle(R.string.time_picker_title)
            setPreselectedTime(TimeValue(hour, minute))
            setThemeColor(R.color.moselle_green)
            setTitleColor(R.color.white)
        }.build().apply {
            setListener { hour, minute ->
                preferenceHelper.setHourAndMinute(hour, minute)
                setAlarm()
            }
        }.show(childFragmentManager, "timePicker")
    }

    private fun setAlarm() {
        val preferenceHelper = PreferenceHelper(requireContext())
        val hour = preferenceHelper.getHour()
        val minute = preferenceHelper.getMinute()

        if (hour != -1 || minute != -1) {
            val alarmManager =
                requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(requireContext(), AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)

            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
            )
        }
    }
}