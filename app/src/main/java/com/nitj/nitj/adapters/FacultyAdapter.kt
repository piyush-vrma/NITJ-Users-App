import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.nitj.nitj.R
import com.nitj.nitj.models.FacultyData
import com.nitj.nitj.screens.fragments.FacultyFragmentDirections
import com.nitj.nitj.screens.fragments.HomeFragmentDirections
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class FacultyAdapter(
    private val context: Context,
    private val listItem: ArrayList<FacultyData>
) : RecyclerView.Adapter<FacultyAdapter.FacultyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacultyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tile_faculty, parent, false)
        return FacultyViewHolder(view)
    }

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: FacultyViewHolder, position: Int) {
        val facultyData = listItem[position]

        holder.txtFacultyName.text = facultyData.name
        holder.txtDesignation.text = facultyData.designation
        holder.txtFacultyEmailId.text = facultyData.emailId

        val str = SpannableString(holder.researchTitle.text)
        str.setSpan(UnderlineSpan(), 0, holder.researchTitle.text.length, 0)

        holder.researchTitle.text = str
        holder.txtFacultyResearchInterest.text = facultyData.researchInterests

        try {
            Picasso.get().load(facultyData.profileImage).error(R.drawable.logo)
                .into(holder.imgFacultyProfileImage)
        } catch (e: Exception) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
        }

        holder.imgFacultyProfileImage.setOnClickListener {
            val action = FacultyFragmentDirections.actionFacultyDestToFullImageViewFragment(facultyData.name,facultyData.profileImage)
            it.findNavController().navigate(action)
        }

        holder.facultyTile.setOnClickListener {

//            val args = Bundle()
//            args.putString("name", facultyData.name)
//            args.putString("department", facultyData.department)
//            args.putString("designation", facultyData.designation)
//            args.putString("qualification1", facultyData.qualification1)
//            args.putString("qualification2", facultyData.qualification1)
//            args.putString("qualification3", facultyData.qualification1)
//            args.putString("emailId", facultyData.emailId)
//            args.putString("researchInterests", facultyData.researchInterests)
//            args.putString("fax", facultyData.fax)
//            args.putString("profileImage", facultyData.profileImage)
//
//
//            val fragment: Fragment = FacFullDetailFragment()
//            fragment.arguments = args
//            val fragmentManager: FragmentManager =
//                (context as AppCompatActivity).supportFragmentManager
//            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.my_nav_host_fragment, fragment)
//            fragmentTransaction.addToBackStack(null)
//            fragmentTransaction.commit()

            val action = FacultyFragmentDirections.actionFacultyDestToFacFullDetailDest(
                facultyData.name,
                facultyData.department,
                facultyData.designation,
                facultyData.qualification1,
                facultyData.qualification2,
                facultyData.qualification3,
                facultyData.emailId,
                facultyData.researchInterests,
                facultyData.fax,
                facultyData.profileImage
            )
            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    class FacultyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgFacultyProfileImage: CircleImageView = view.findViewById(R.id.imgFacultyProfileImage)
        val txtFacultyName: TextView = view.findViewById(R.id.txtFacultyName)
        val txtDesignation: TextView = view.findViewById(R.id.txtDesignation)
        val txtFacultyEmailId: TextView = view.findViewById(R.id.txtFacultyEmailId)
        val txtFacultyResearchInterest: TextView =
            view.findViewById(R.id.txtFacultyResearchInterest)
        val researchTitle: TextView = view.findViewById(R.id.researchTitle)
        val facultyTile: CardView = view.findViewById(R.id.facultyTile)
    }
}